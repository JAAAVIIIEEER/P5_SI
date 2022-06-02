/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import modelo.HibernateUtil;
import controlador.ExcelManager;
/**
 *
 * @author jp
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExcelManager nuevoControlador = new ExcelManager();
        nuevoControlador.openFile();
        nuevoControlador.leerHojas();
        nuevoControlador.analizarDatosTrabajadores();
        nuevoControlador.generarNominas();
        nuevoControlador.escribirDatosTrabajadores();
        nuevoControlador.closeFile();
        nuevoControlador.imprimirHojasHashMap();
        HibernateUtil.shutdown();
    }
}
