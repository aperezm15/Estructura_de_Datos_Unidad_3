package org.udc;

import org.udc.enums.EstadoSolicitud;
import org.udc.enums.TipoPaquete;
import org.udc.modelo.SolicitudViaje;
import org.udc.servicios.GestorViajes;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GestorViajes gestor = new GestorViajes();
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        System.out.println("=============================================");
        System.out.println("   SISTEMA DE GESTIÓN - AGENCIA DE VIAJES    ");
        System.out.println("=============================================");

        do {
            System.out.println("\n-------------------------------------------------");
            System.out.println("                MENÚ DE OPCIONES                 ");
            System.out.println("-------------------------------------------------");
            System.out.println("1. Registrar solicitud");
            System.out.println("2. Ver todas las solicitudes registradas");
            System.out.println("3. Ver solicitudes pendientes");
            System.out.println("4. Procesar siguiente solicitud");
            System.out.println("5. Ver historial de solicitudes procesadas");
            System.out.println("6. Buscar solicitud por código usando Map");
            System.out.println("7. Buscar solicitud por cliente usando Stream");
            System.out.println("8. Filtrar solicitudes por paquete usando Stream");
            System.out.println("9. Ordenar solicitudes usando Stream");
            System.out.println("10. Ver estadísticas usando Stream y Map");
            System.out.println("11. Ver agrupamientos usando Stream y Map");
            System.out.println("12. Cancelar solicitud pendiente");
            System.out.println("13. Deshacer último procesamiento");
            System.out.println("14. Ver cantidad de elementos y estados");
            System.out.println("15. Salir");
            System.out.print("Seleccione una opción (1-15): ");

            try {
                // Validación para evitar entradas incorrectas
                if (!scanner.hasNextInt()) {
                    System.out.println("Error: Por favor, ingrese un número entero válido.");
                    scanner.next();
                    continue;
                }
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        System.out.println("\n--- REGISTRAR NUEVA SOLICITUD ---");
                        System.out.print("Ingrese código de solicitud (Ej: SV101): ");
                        String codigo = scanner.nextLine().trim();
                        System.out.print("Ingrese nombre del cliente: ");
                        String cliente = scanner.nextLine().trim();
                        System.out.print("Ingrese destino del viaje: ");
                        String destino = scanner.nextLine().trim();
                        System.out.print("Ingrese tipo de paquete (FAMILIAR, EJECUTIVO, MOCHILERO): ");
                        String paqueteInput = scanner.nextLine().trim().toUpperCase();

                        if (codigo.isEmpty() || cliente.isEmpty() || destino.isEmpty() || paqueteInput.isEmpty()) {
                            System.out.println("Error: Ningún campo puede quedar vacío.");
                            break;
                        }

                        TipoPaquete paqueteEnum;
                        try {
                            paqueteEnum = TipoPaquete.valueOf(paqueteInput);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: El tipo de paquete '" + paqueteInput + "' no es válido. Registros permitidos: FAMILIAR, EJECUTIVO, MOCHILERO.");
                            break;
                        }

                        SolicitudViaje nueva = new SolicitudViaje(codigo, cliente, destino, paqueteEnum, EstadoSolicitud.PENDIENTE);
                        gestor.registrarSolicitudViaje(nueva);
                        System.out.println("Solicitud registrada exitosamente en el sistema.");
                        break;

                    case 2:
                        System.out.println("\n--- TODAS LAS SOLICITUDES REGISTRADAS (LIST) ---");
                        gestor.verTodosLosElementos();
                        break;

                    case 3:
                        System.out.println("\n--- SOLICITUDES EN COLA DE ESPERA (QUEUE) ---");
                        gestor.verElementosPendientes();
                        break;

                    case 4:
                        System.out.println("\n--- PROCESANDO SIGUIENTE SOLICITUD ---");
                        gestor.procesarSiguiente();
                        break;

                    case 5:
                        System.out.println("\n--- HISTORIAL DE RESERVAS PROCESADAS (DEQUE) ---");
                        gestor.verHistorialProcesados();
                        break;

                    case 6:
                        System.out.print("\nIngrese el código de solicitud a buscar vía Map: ");
                        String codBusqueda = scanner.nextLine().trim();
                        SolicitudViaje encMap = gestor.buscarPorIdMap(codBusqueda);
                        if (encMap != null) {
                            System.out.println("Encontrado instantáneamente (Map): " + encMap);
                        } else {
                            System.out.println("No se encontró ninguna solicitud con el código: " + codBusqueda);
                        }
                        break;

                    case 7:
                        System.out.print("\nIngrese el nombre del cliente a buscar vía Stream: ");
                        String nomBusqueda = scanner.nextLine().trim();
                        Optional<SolicitudViaje> encStream = gestor.buscarPorClienteStream(nomBusqueda);
                        if (encStream.isPresent()) {
                            System.out.println("Encontrado secuencialmente (Stream): " + encStream.get());
                        } else {
                            System.out.println("No se encontraron registros para el cliente: " + nomBusqueda);
                        }
                        break;

                    case 8:
                        System.out.print("\nIngrese el tipo de paquete a filtrar (Familiar, Ejecutivo, Mochilero): ");
                        String filtraPaq = scanner.nextLine().trim();
                        System.out.println("--- RESULTADO DEL FILTRADO VIA STREAM ---");
                        List<SolicitudViaje> filtrados = gestor.filtrarPorPaqueteStream(filtraPaq);
                        if (!filtrados.isEmpty()) {
                            filtrados.forEach(System.out::println);
                        }
                        break;

                    case 9:
                        System.out.println("\nSeleccione el criterio de ordenamiento:");
                        System.out.println("a. Por Nombre de Cliente (Alfabético Ascendente)");
                        System.out.println("b. Por Código de Solicitud (Descendente)");
                        System.out.print("Criterio elegido (a/b): ");
                        String criterio = scanner.nextLine().trim().toLowerCase();

                        System.out.println("=== LISTADO ORDENADO VIA STREAM ===");
                        if (criterio.equals("a")) {
                            gestor.ordenarPorClienteAsc().forEach(System.out::println);
                        } else if (criterio.equals("b")) {
                            gestor.ordenarPorCodigoDesc().forEach(System.out::println);
                        } else {
                            System.out.println("Opción de ordenamiento inválida.");
                        }
                        break;

                    case 10:
                        System.out.println("\n--- ANÁLISIS ESTADÍSTICO VIA STREAM ---");
                        gestor.verEstadisticas();
                        break;

                    case 11:
                        System.out.println("\n--- AGRUPAMIENTOS POR CATEGORÍA ---");
                        gestor.verAgrupamientosPorPaquete();
                        break;

                    case 12:
                        System.out.print("\nIngrese el código de la solicitud pendiente que desea cancelar: ");
                        String codCancel = scanner.nextLine().trim();
                        gestor.cancelarElementoPendiente(codCancel);
                        break;

                    case 13:
                        System.out.println("\n--- DESHACIENDO ÚLTIMA OPERACIÓN (DESHACER LIFO) ---");
                        gestor.deshacerUltimoProcesamiento();
                        break;

                    case 14:
                        System.out.println("\n--- AUDITORÍA Y CONTEO GLOBAL DE ESTRUCTURAS ---");
                        gestor.verCantidadElementos();
                        break;

                    case 15:
                        System.out.println("\nSaliendo del sistema de la Agencia de Viajes. ¡Hasta pronto!");
                        break;

                    default:
                        System.out.println("Opción inválida. Digite un número entre 1 y 15.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("ERROR CONTROLADO: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("💥 Ocurrió una anomalía inesperada: " + e.getMessage());
            }
        } while (opcion != 15);

        scanner.close();
    }
}