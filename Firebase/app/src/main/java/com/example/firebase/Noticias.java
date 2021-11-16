package com.example.firebase;



public class Noticias {

    private String Formacion;
    private String Informatica;
    private String Mantenimiento;

    public Noticias() {
    }

    public Noticias(String formacion, String informatica, String mantenimiento) {
        Formacion = formacion;
        Informatica = informatica;
        Mantenimiento = mantenimiento;
    }

    public void setFormacion(String formacion) {
        Formacion = formacion;
    }
    public void setInformatica(String informatica) {
        Informatica = informatica;
    }
    public void setMantenimiento(String mantenimiento) {
        Mantenimiento = mantenimiento;
    }
    public String getFormacion() {
        return Formacion;
    }
    public String getInformatica() {
        return Informatica;
    }
    public String getMantenimiento() {
        return Mantenimiento;
    }

    @Override
    public String toString() {
        return "Avisos{" +
                "Formacion='" + Formacion + '\'' +
                ", Informatica='" + Informatica + '\'' +
                ", Mantenimiento='" + Mantenimiento + '\'' +
                '}';
    }
}
