/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import modelo.Trabajadorbbdd;
import modelo.Categorias;
import modelo.Cuota;
import modelo.Empresas;
import modelo.ExtraTrienio;
import modelo.Retencion;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 *
 * @author jp
 */

public class ExcelDao {
    private Workbook excelFile;

    public void openFile() {
        try {
            this.excelFile = new XSSFWorkbook(new FileInputStream("./resources/SistemasInformacionII.xlsx"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void closeFile() {
        try {
            FileOutputStream outputStream = new FileOutputStream("./resources/SistemasInformacionIIModificado.xlsx");
            excelFile.write(outputStream);
            excelFile.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public HashMap leerPrimeraHoja() {
        Sheet hoja = excelFile.getSheetAt(0);
        HashMap datos = new HashMap();
        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            datos.put(hoja.getRow(i).getCell(0).toString(),hoja.getRow(i).getCell(1).toString()); 
        }
        return datos;
    }
    
    public HashMap leerSegundaHoja() {
        Sheet hoja = excelFile.getSheetAt(1);
        HashMap datos = new HashMap();
        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            datos.put(hoja.getRow(i).getCell(0).toString(),hoja.getRow(i).getCell(1).toString()); 
        }
        return datos;
    }
    
    public HashMap leerTerceraHoja() {
        Sheet hoja = excelFile.getSheetAt(2);
        HashMap<String, List<String>> datos = new HashMap<String, List<String>>();
        List<String> valores = new ArrayList();
        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            valores.add(hoja.getRow(i).getCell(1).toString());
            valores.add(hoja.getRow(i).getCell(2).toString());
            datos.put(hoja.getRow(i).getCell(0).toString(),valores);
            
        }
        return datos;
    }
    
    public HashMap leerCuartaHoja() {
        Sheet hoja = excelFile.getSheetAt(3);
        HashMap datos = new HashMap();
        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            datos.put(hoja.getRow(i).getCell(0).toString(),hoja.getRow(i).getCell(1).toString()); 
        }
        return datos;
    }

    public ArrayList<Trabajadorbbdd> leerQuintaHoja() {
        ArrayList<Trabajadorbbdd> datosTrabajadores = new ArrayList<>();
        Sheet hoja = excelFile.getSheetAt(4);
        
        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            Trabajadorbbdd nuevoTrabajador = new Trabajadorbbdd();
            nuevoTrabajador.setIdTrabajador(i + 1);
            Empresas emp = new Empresas();
            for (int j = 0; j < 13; j++) {
                if (hoja.getRow(i) != null) {
                    if(hoja.getRow(i).getCell(j) != null){
                        switch (j) {
                            case 0:
                                nuevoTrabajador.setNifnie(hoja.getRow(i).getCell(j).toString());
                                break;
                            case 1:
                                nuevoTrabajador.setNombre(hoja.getRow(i).getCell(j).toString());
                                break;
                            case 2:
                                nuevoTrabajador.setApellido1(hoja.getRow(i).getCell(j).toString());
                                break;
                            case 3:
                                nuevoTrabajador.setApellido2(hoja.getRow(i).getCell(j).toString());
                                break;
                            case 4:
                                emp.setCif(hoja.getRow(i).getCell(j).toString());
                                break;
                            case 5:
                                emp.setNombre(hoja.getRow(i).getCell(j).toString());
                                break;
                            case 6:
                                nuevoTrabajador.setFechaAlta(hoja.getRow(i).getCell(j).getDateCellValue());
                                break;
                            case 7:
                                Categorias cat = new Categorias();
                                cat.setNombreCategoria(hoja.getRow(i).getCell(j).toString());
                                nuevoTrabajador.setCategorias(cat);
                                break;
                            case 8:
                                nuevoTrabajador.setProrrateo((hoja.getRow(i).getCell(j).toString().equals("SI")));
                                break;
                            case 9:
                                nuevoTrabajador.setCodigoCuenta(hoja.getRow(i).getCell(j).toString());
                                break;
                            case 10:
                                nuevoTrabajador.setPaisCuenta(hoja.getRow(i).getCell(j).toString());
                                break;
                            case 11:
                                nuevoTrabajador.setIban(hoja.getRow(i).getCell(j).toString());
                                break;
                            case 12:
                                nuevoTrabajador.setEmail(hoja.getRow(i).getCell(j).toString());
                                break;
                        }
                    }
                }
            }
            nuevoTrabajador.setEmpresas(emp);
            datosTrabajadores.add(nuevoTrabajador);
        }
        return datosTrabajadores;
    }

    public void escribirHoja(ArrayList<Trabajadorbbdd> trabajadores) {
        Sheet hoja = excelFile.getSheetAt(4);
        int i = 1;
        for (Trabajadorbbdd nuevoTrabajador : trabajadores) {
            if (nuevoTrabajador.getNifnie() != null)
                hoja.getRow(i).getCell(0).setCellValue(nuevoTrabajador.getNifnie());
            if (nuevoTrabajador.getCodigoCuenta() != null) 
                hoja.getRow(i).getCell(9).setCellValue(nuevoTrabajador.getCodigoCuenta());
            for(int j = 11; j < 13; j++){
                if(hoja.getRow(i) == null) {
                    hoja.createRow(i);
                    if (hoja.getRow(i).getCell(j) == null) 
                        hoja.getRow(i).createCell(j);
                } else {
                    if (hoja.getRow(i).getCell(j) == null) 
                        hoja.getRow(i).createCell(j);
                }
            }
            if (nuevoTrabajador.getIban() != null) 
                hoja.getRow(i).getCell(11).setCellValue(nuevoTrabajador.getIban());
            if (nuevoTrabajador.getEmail() != null) 
                hoja.getRow(i).getCell(12).setCellValue(nuevoTrabajador.getEmail());
            i++;
        }
    }
    public ArrayList<Retencion> getRetenciones() {
        
        Sheet hoja = excelFile.getSheetAt(3);
        ArrayList<Retencion> retenciones = new ArrayList<>();
        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            Retencion ret = new Retencion();
            ret.setBrutoAnual((int)hoja.getRow(i).getCell(0).getNumericCellValue());
            ret.setRetencion(hoja.getRow(i).getCell(1).getNumericCellValue());
            retenciones.add(ret);
        }
        return retenciones;
    }
    
    public ArrayList<Cuota> getCuotas() {
        Sheet hoja = excelFile.getSheetAt(0);
        ArrayList<Cuota> cuotas = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            Cuota cuota = new Cuota();
            cuota.setNombreCuota(hoja.getRow(i).getCell(0).toString());
            cuota.setCosto(hoja.getRow(i).getCell(1).getNumericCellValue());
            cuotas.add(cuota);
        }
        return cuotas;
    }

    public ArrayList<Categorias> getSalarios() {
        Sheet hoja = excelFile.getSheetAt(2);
        ArrayList<Categorias> categorias = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            Categorias categoria = new Categorias(i,hoja.getRow(i).getCell(0).toString(), hoja.getRow(i).getCell(2).getNumericCellValue(), hoja.getRow(i).getCell(1).getNumericCellValue());
            categorias.add(categoria);
        }
        return categorias;
    }
    
    public ArrayList<ExtraTrienio> getExtraTrienios() {
        Sheet hoja = excelFile.getSheetAt(1);
        ArrayList<ExtraTrienio> extras = new ArrayList<>();
        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            ExtraTrienio extra = new ExtraTrienio();
            extra.setNumTrienios((int)hoja.getRow(i).getCell(0).getNumericCellValue());
            extra.setExtra((int)hoja.getRow(i).getCell(1).getNumericCellValue());
            extras.add(extra);
        }
        return extras;
    }
}
