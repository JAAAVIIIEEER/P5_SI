/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.PDFDao;
import java.util.Set;
import modelo.Nomina;

/**
 *
 * @author jp
 */
public class PDFControlador {
    PDFDao dao = new PDFDao();
    
    public PDFControlador() {
        this.dao = new PDFDao();
    }
    
    public void crearPDFs(Set<Nomina> nominas) {
        for (Nomina n:nominas) {
            if(n.getTrabajadorbbdd().getNifnie() != null) {
                if(!"".equals(n.getTrabajadorbbdd().getNifnie()))
                    dao.createPDF(n);
            }
                
        }
    }
    
    public void crearPDF(){
        dao.crearPDF();
    }
}
