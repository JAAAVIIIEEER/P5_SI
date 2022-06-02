/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import dao.CategoriaDao;
import dao.EmpresasDao;
import dao.NominaDao;
import dao.TrabajadorbbddDao;
import java.util.Set;
import modelo.Nomina;
/**
 *
 * @author jp
 */
public class NominaControlador {
    private NominaDao daoNomina;
    private TrabajadorbbddDao daoTrabajador;
    private EmpresasDao daoEmpresa;
    private CategoriaDao daoCategoria;    
    
    public NominaControlador() {
        this.daoNomina = new NominaDao();
        this.daoTrabajador = new TrabajadorbbddDao();
        this.daoEmpresa = new EmpresasDao();
        this.daoCategoria = new CategoriaDao();
    }
    
    public void aniadirNominas(Set<Nomina> nominas) {        
        for (Nomina n : nominas) {
            if(n.getTrabajadorbbdd().getNifnie() != null && n.getTrabajadorbbdd().getApellido1() != null && n.getTrabajadorbbdd().getNombre() != null) {
                if(!"".equals(n.getTrabajadorbbdd().getNifnie())) {
                    if (daoEmpresa.buscarEmpresa(n.getTrabajadorbbdd().getEmpresas().getCif())) {
                        daoEmpresa.aniadirEmpresa(n.getTrabajadorbbdd().getEmpresas());
                    }
                    if (daoCategoria.buscarCategoria(n.getTrabajadorbbdd().getCategorias().getNombreCategoria())) {
                        daoCategoria.aniadirCategoria(n.getTrabajadorbbdd().getCategorias());
                    }
                    //if (daoTrabajador.buscarTrabajador(n.getTrabajadorbbdd())) {
                        //daoTrabajador.aniadirTrabajador(n.getTrabajadorbbdd());
                    //}
                    //if (daoNomina.buscarNomina(n)) {
                        //daoNomina.aniadirNomina(n);
                    //}
                }       
            }
        }
    }
}
