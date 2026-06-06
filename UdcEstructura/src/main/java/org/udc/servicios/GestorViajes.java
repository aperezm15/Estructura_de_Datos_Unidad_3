package org.udc.servicios;

import org.udc.enums.EstadoSolicitud;
import org.udc.enums.TipoPaquete;
import org.udc.modelo.SolicitudViaje;

import java.util.*;
import java.util.stream.Collectors;

public class GestorViajes {
    // Registro general de todos los elementos
    private final List<SolicitudViaje> elementos = new ArrayList<>();

    // Elementos pendientes por procesar con FIFO creandolo con Queue
    private final Queue<SolicitudViaje> pendientes = new LinkedList<>();

    // Historial de lementos procesados en pila con LIFO usando Deque
    private final Deque<SolicitudViaje> historial = new ArrayDeque<>();

    // Busqueda rapida por identificador unico
    private final Map<String, SolicitudViaje> indicePorCodigo = new HashMap<>();


    // Metodo para registrar solicitud de viaje
    public void registrarSolicitudViaje(SolicitudViaje solicitud) {
        if (indicePorCodigo.containsKey(solicitud.getCodigoSolicitudViaje())) {
            throw new IllegalStateException("Solicitud de viaje ya existe");
        }
        elementos.add(solicitud);
        pendientes.offer(solicitud);
        indicePorCodigo.put(solicitud.getCodigoSolicitudViaje(), solicitud);
    }

    // Metodo para ver elementos pendientes
    public void verElementosPendientes() {
        if (pendientes.isEmpty()) {
            System.out.println("No hay solicitudes en la cola");
            return;
        }
        System.out.println("Siguiente solicitud por atender: "+ pendientes.peek());
        System.out.println("Solicitudes en cola: "+pendientes.size());
        System.out.println("-------------------------------------");
        System.out.println("- Solicitudes Pendientes -");
        pendientes.forEach(System.out::println);
        System.out.println("-------------------------------------");
    }

    // Metodo para procesar Siguente elemento.
    public void procesarSiguiente() {
        SolicitudViaje procesado = pendientes.poll();
        if (procesado == null) {
            throw new IllegalStateException("No hay elementos pendientes.");
        }
        procesado.setEstado(EstadoSolicitud.PROCESADO);
        historial.push(procesado);
        System.out.println("Solicitud procesada con éxito: " + procesado);
    }
    // Ver historial de elementos procesados
    public void verHistorialProcesados() {
        if (historial.isEmpty()) {
            System.out.println("El historial de procesamiento está vacío.");
            return;
        }
        System.out.println("Último procesado (peek): " + historial.peek());
        System.out.println("Total procesados (size): " + historial.size());
        System.out.println("-------------------------------------");
        System.out.println("- Historial (Pila LIFO) -");
        historial.forEach(System.out::println);
        System.out.println("-------------------------------------");
    }
    // Buscar Solicitud por Codigo unico.
    public SolicitudViaje buscarPorIdMap(String codigo) {
        if (!indicePorCodigo.containsKey(codigo)) {
            return null;
        }
        return indicePorCodigo.get(codigo);
    }
    // Buscamos elemento usando el nombre con Stream
    public Optional<SolicitudViaje> buscarPorClienteStream(String nombre) {
        return elementos.stream()
                .filter(e -> e.getNombreCliente().equalsIgnoreCase(nombre))
                .findFirst();
    }
    // buscamos por paquete usando Stream
    public List<SolicitudViaje> filtrarPorPaqueteStream(String tipoPaquete) {
        try {
            TipoPaquete paqueteEnum = TipoPaquete.valueOf(tipoPaquete.trim().toUpperCase());

            return elementos.stream()
                    .filter(e -> e.getTipoPaquete() == paqueteEnum)
                    .toList();

        } catch (IllegalArgumentException e) {
            System.out.println("El tipo de paquete '" + tipoPaquete + "' no es válido.");
            return List.of();
        }
    }

    //Ordenar elementos usando Stream, Tenemos dos formas
    //Por Nombre de Cliente (Ascendente)
    public List<SolicitudViaje> ordenarPorClienteAsc() {
        return elementos.stream()
                .sorted(Comparator.comparing(SolicitudViaje::getNombreCliente))
                .toList();
    }

    //Por Código de Solicitud (Descendente)
    public List<SolicitudViaje> ordenarPorCodigoDesc() {
        return elementos.stream()
                .sorted(Comparator.comparing(SolicitudViaje::getCodigoSolicitudViaje).reversed())
                .toList();
    }

    // Ver estadísticas usando Stream y Map
    public void verEstadisticas() {
        Map<EstadoSolicitud, Long> conteoPorEstado = elementos.stream()
                .collect(Collectors.groupingBy(SolicitudViaje::getEstado, Collectors.counting()));

        long totalGeneral = elementos.size();
        long totalPendientes = pendientes.size();
        long totalProcesados = historial.size();
        long totalCancelados = elementos.stream()
                .filter(e -> e.getEstado() == EstadoSolicitud.CANCELADO)
                .count();

        System.out.println("====== ESTADÍSTICAS DEL SISTEMA ======");
        System.out.println("Total general registrado (List): " + totalGeneral);
        System.out.println("Total pendientes en cola (Queue): " + totalPendientes);
        System.out.println("Total procesados en historial (Deque): " + totalProcesados);
        System.out.println("Total cancelados en el sistema: " + totalCancelados);
        System.out.println("Conteo consolidado por estados en Map: " + conteoPorEstado);
    }

    // Ver agrupamientos usando Stream y Map
    public void verAgrupamientosPorPaquete() {
        Map<TipoPaquete, List<SolicitudViaje>> porCategoria = elementos.stream()
                .collect(Collectors.groupingBy(SolicitudViaje::getTipoPaquete));

        if (porCategoria.isEmpty()) {
            System.out.println("No hay datos para agrupar.");
            return;
        }

        // Recorrido de claves y valores del mapa generado
        porCategoria.forEach((categoria, lista) -> {
            System.out.println("\nTipo de Paquete: " + categoria);
            lista.forEach(e -> System.out.println("   -> " + e));
        });
    }

    // Cancelar elemento pendiente
    public void cancelarElementoPendiente(String codigo) {
        SolicitudViaje elemento = indicePorCodigo.get(codigo);
        if (elemento == null) {
            throw new IllegalArgumentException("No existe una solicitud con ese identificador.");
        }
        if (elemento.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden cancelar solicitudes que estén pendientes.");
        }

        elemento.setEstado(EstadoSolicitud.CANCELADO);
        pendientes.removeIf(e -> e.getCodigoSolicitudViaje().equalsIgnoreCase(codigo));
        System.out.println("Solicitud cancelada con éxito: " + elemento);
    }

    //  Deshacer último procesamiento (Mecanismo LIFO a FIFO)
    public void deshacerUltimoProcesamiento() {
        if (historial.isEmpty()) {
            throw new IllegalStateException("No hay elementos en el historial para deshacer.");
        }
        // Sacamos el último de la pila (LIFO)
        SolicitudViaje ultimo = historial.pop();
        // Lo devolvemos al estado inicial
        ultimo.setEstado(EstadoSolicitud.PENDIENTE);
        // Regresa a la cola de atención (FIFO)
        pendientes.offer(ultimo);
        System.out.println("Se deshizo el procesamiento. Solicitud devuelta a la cola: " + ultimo);
    }

    //  Ver cantidad de elementos y verificación de condiciones con matchers
    public void verCantidadElementos() {
        System.out.println("Elementos en List (Global): " + elementos.size());
        System.out.println("Elementos en Queue (Pendientes): " + pendientes.size());
        System.out.println("Elementos en Deque (Historial): " + historial.size());
        System.out.println("Elementos en Map (Índice): " + indicePorCodigo.size());

        // Validaciones generales mediante Streams
        boolean existePendiente = elementos.stream().anyMatch(e -> e.getEstado() == EstadoSolicitud.PENDIENTE);
        boolean todosTienenCodigo = elementos.stream().allMatch(e -> e.getCodigoSolicitudViaje() != null && !e.getCodigoSolicitudViaje().isBlank());
        boolean noHayCancelados = elementos.stream().noneMatch(e -> e.getEstado() == EstadoSolicitud.CANCELADO);

        System.out.println("¿Existen solicitudes pendientes actuales?: " + (existePendiente ? "SÍ" : "NO"));
        System.out.println("¿Todas las solicitudes poseen códigos válidos?: " + (todosTienenCodigo ? "SÍ" : "NO"));
        System.out.println("¿El sistema está libre de cancelaciones?: " + (noHayCancelados ? "SÍ" : "NO"));
    }

    public void verTodosLosElementos() {
        if (elementos.isEmpty()) {
            System.out.println("No hay solicitudes registradas en el sistema.");
            return;
        }
        elementos.forEach(System.out::println);
    }

    // Convertir una lista en un mapa controlando colisiones
    public void reconstruirIndiceAlterno() {
        Map<String, SolicitudViaje> mapaAlterno = elementos.stream()
                .collect(Collectors.toMap(
                        SolicitudViaje::getCodigoSolicitudViaje,
                        e -> e,
                        (existente, repetido) -> existente // Manejo de colisión: conserva el primero
                ));
        System.out.println("Índice alterno reconstruido vía Stream de manera exitosa.");
    }
}
