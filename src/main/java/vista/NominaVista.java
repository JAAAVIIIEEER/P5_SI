package vista;

import java.util.Scanner;
import java.util.Set;
import modelo.Nomina;

public class NominaVista {
    
    public String solicitarFecha() {
        Scanner leer = new Scanner(System.in);
        String fecha;
        System.out.println("Introduzca una fecha:\n");
        fecha = leer.nextLine();
        return fecha;
    }
     
    public void imprimeNominas(Set<Nomina> nominas) {
        for (Nomina nom:nominas) {
            System.out.print(nom.getTrabajadorbbdd().getNombre()+" "+nom.getTrabajadorbbdd().getApellido1()+" "+nom.getTrabajadorbbdd().getApellido2()+ " NIF/NIE: " + nom.getTrabajadorbbdd().getNifnie());
            if (nom.isExtra()) {
                System.out.println(" EXTRA");
            } else {
                System.out.println();
            }
            System.out.println("Fecha Alta: " + nom.getTrabajadorbbdd().getFechaAlta());
            System.out.println("IBAN: " + nom.getTrabajadorbbdd().getIban() + " Bruto Anual: " + nom.getBrutoAnual());
            System.out.println("Empresa: " +nom.getTrabajadorbbdd().getEmpresas().getNombre() + " CIF: " + nom.getTrabajadorbbdd().getEmpresas().getCif() + " Categoria: " + nom.getTrabajadorbbdd().getCategorias().getNombreCategoria());
            System.out.println("Fecha: "+nom.getMes()+"/"+nom.getAnio());
            System.out.println("Trienios: "+nom.getNumeroTrienios()+" Importe: "+nom.getImporteTrienios());
            System.out.println("Salario Base Anual: "+nom.getSalarioBaseAnual()+" Complemento Base Anual: "+nom.getComplementoAnual());
            System.out.println("Salario Mes: "+nom.getImporteSalarioMes()+" Complemento Mes: " + nom.getImporteComplementoMes());
            System.out.println("Prorrateo: "+nom.getValorProrrateo());
            System.out.println("IRPF: " + nom.getIrpf() + " Importe IRPF: " + nom.getImporteIrpf());
            System.out.println("Base Empresario: " + nom.getBaseEmpresario());
            System.out.println("Seguridad Social Empresario: " + nom.getSeguridadSocialEmpresario() + " Importe Seguridad Social Empresario: "+nom.getImporteSeguridadSocialEmpresario());
            System.out.println("Desempleo Empresario: " + nom.getDesempleoEmpresario() + " Importe Desempleo Empresario: " + nom.getImporteDesempleoEmpresario());
            System.out.println("Formación Empresario: " + nom.getFormacionEmpresario() + " Importe Formacion Empresario: " + nom.getImporteFormacionEmpresario());
            System.out.println("Accidentes Trabajo Empresario: " + nom.getAccidentesTrabajoEmpresario() + " Importe Accidentes Trabajo Empresario: " + nom.getImporteAccidentesTrabajoEmpresario());
            System.out.println("FOGASA Empresario: " + nom.getFogasaempresario() + " Importe FOGASA Empresario: " + nom.getImporteFogasaempresario());
            System.out.println("Seguridad Social Trabajador: " + nom.getImporteSeguridadSocialTrabajador()+" Importe Seguridad Social Trabajador: " + nom.getImporteSeguridadSocialTrabajador());
            System.out.println("Desempleo Trabajador: " + nom.getDesempleoTrabajador()+" Importe Desempleo Trabajador: " + nom.getImporteDesempleoTrabajador());
            System.out.println("Formacion Trabajador: " + nom.getFormacionTrabajador() + " Importe Formación Trabajador: " + nom.getImporteFormacionTrabajador());
            System.out.println("Bruto Nomina: "+ nom.getBrutoNomina() + " Liquido Nomina: " + nom.getLiquidoNomina());
            System.out.println("Coste Total Trabajador: " + nom.getCosteTotalEmpresario());
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
        }
    }
}
