package org.udc.modelo;

import org.udc.enums.EstadoSolicitud;
import org.udc.enums.TipoPaquete;

import java.util.Objects;
//Nuestra clase modelo
public class SolicitudViaje {
    private String codigoSolicitudViaje;
    private String nombreCliente;
    private String destino;
    //Se hicieron enums para estos atributos.
    private TipoPaquete tipoPaquete;
    private EstadoSolicitud estado;

    //Nuestro Constructor
    public SolicitudViaje(String codigoSolicitudViaje, String nombreCliente,
                          String destino, TipoPaquete tipoPaquete, EstadoSolicitud estado) {
        this.codigoSolicitudViaje = codigoSolicitudViaje;
        this.nombreCliente = nombreCliente;
        this.destino = destino;
        this.tipoPaquete = tipoPaquete;
        this.estado = estado;
    }

    //Getter y Setters

    public String getCodigoSolicitudViaje() {
        return codigoSolicitudViaje;
    }

    public void setCodigoSolicitudViaje(String codigoSolicitudViaje) {
        this.codigoSolicitudViaje = codigoSolicitudViaje;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public TipoPaquete getTipoPaquete() {
        return tipoPaquete;
    }

    public void setTipoPaquete(TipoPaquete tipoPaquete) {
        this.tipoPaquete = tipoPaquete;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    //Metodos obligatorios Equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SolicitudViaje that = (SolicitudViaje) o;
        return Objects.equals(codigoSolicitudViaje, that.codigoSolicitudViaje);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigoSolicitudViaje);
    }

    @Override
    public String toString() {
        return "SolicitudViaje{" +
                "codigoSolicitudViaje='" + codigoSolicitudViaje + '\'' +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", destino='" + destino + '\'' +
                ", tipoPaquete=" + tipoPaquete +
                ", estado=" + estado +
                '}';
    }
}
