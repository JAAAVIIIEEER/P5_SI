/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import dao.ExcelDao;
import dao.XmlDao;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Categorias;
import modelo.Cuota;
import modelo.ExtraTrienio;
import modelo.Nomina;
import modelo.Retencion;
import modelo.Trabajadorbbdd;
import vista.NominaVista;
/**
 *
 * @author jp
 */
public class ExcelManager {
    private final String letrasDNI = "TRWAGMYFPDXBNJZSQVHLCKE";
    private final String multiplicadorCodigo = "12485109736";
    
    private ExcelDao excelDao;
    private NominaControlador nominaControlador;
    private NominaVista nominaVista;
    
    
    private ArrayList<Trabajadorbbdd> datosTrabajadores;
    private HashMap datosHoja1;
    private HashMap datosHoja2;
    private HashMap datosHoja3;
    private HashMap datosHoja4;
    private XmlDao xml;
    
    private ArrayList<Categorias> categorias;
    private ArrayList<ExtraTrienio> extras;
    private ArrayList<Cuota> cuotas;
    private ArrayList<Retencion> retenciones;
    private Date fecha;

    public ExcelManager() {
       this.excelDao = new ExcelDao();
       this.nominaControlador = new NominaControlador();
       this.nominaVista = new NominaVista();
    }
    
    public void openFile() {
        this.excelDao.openFile();
    }
    
    public void closeFile() {
        this.excelDao.closeFile();
    }
    
    public void leerHojas() {        
        this.datosTrabajadores = this.excelDao.leerQuintaHoja();
        this.datosHoja1 = this.excelDao.leerPrimeraHoja();
        this.datosHoja2 = this.excelDao.leerSegundaHoja();
        this.datosHoja3 = this.excelDao.leerTerceraHoja();
        this.datosHoja4 = this.excelDao.leerCuartaHoja();
    }
    
    public void analizarDatosTrabajadores() {
    	SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");
        String inputFecha = "";
        try {
            inputFecha = this.nominaVista.solicitarFecha();
            fecha = format.parse(inputFecha);
        } catch (ParseException ex) {
            Logger.getLogger(ExcelManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.xml = new XmlDao(inputFecha);
        categorias = excelDao.getSalarios();
        extras = excelDao.getExtraTrienios();
        cuotas = excelDao.getCuotas();
        retenciones = excelDao.getRetenciones();
        this.comprobarNIF_NIES();  
        this.comprobarCCC();
        this.generarEmail();
    }
    
    public void generarNominas(){
        int idNominas = 0;
        List<String> l = new ArrayList();
        for (Trabajadorbbdd trabajadorActual : datosTrabajadores) {
            trabajadorActual.setNominas(calcularNominas(trabajadorActual));
            this.nominaVista.imprimeNominas(trabajadorActual.getNominas());
            Set<Nomina> nominas = trabajadorActual.getNominas();
            for (Nomina n : nominas) {
                if(n.getTrabajadorbbdd().getNifnie() != null) {
                    if(!"".equals(n.getTrabajadorbbdd().getNifnie())) {
                        n.setIdNomina(idNominas++);
                        this.xml.addNominaValue(n);
                    }       
                }
            }
            if(nominas != null)
                if(!nominas.isEmpty())
                    l.add(trabajadorActual.getCategorias().getNombreCategoria());
     
            this.nominaControlador.aniadirNominas(trabajadorActual.getNominas());
            //PDFControlador pdfCont = new PDFControlador();
            //pdfCont.crearPDFs(trabajadorActual.getNominas());
        }
        this.xml.saveNominasFile();
        PDFControlador pdfCont = new PDFControlador();
        pdfCont.crearPDF();
        
        Map<String, Integer> map = new HashMap<>();

        for (String t : l) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Entry<String, Integer> max = null;
        Entry<String, Integer> min = null;

        for (Entry<String, Integer> e : map.entrySet()) {
            if (min == null || e.getValue() < min.getValue())
                min = e;
        }
        
        for (Entry<String, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }
        
        System.out.println("\n\n\n");
        System.out.println(map);
        //System.out.println("\n\n\n" + min.getKey() + min.getValue() + "\n\n\n");

    }
    
    public void escribirDatosTrabajadores() {
        this.excelDao.escribirHoja(this.datosTrabajadores);
    }
    
    public void imprimirHojasHashMap(){
        System.out.println("Datos Hoja 1 : \n" + this.datosHoja1);
        System.out.println("Datos Hoja 2 : \n" + this.datosHoja2);
        System.out.println("Datos Hoja 3 : \n" + this.datosHoja3);
        System.out.println("Datos Hoja 4 : \n" + this.datosHoja4);

    }
    
    public void comprobarCCC(){
        for (Trabajadorbbdd trabajadorActual : this.datosTrabajadores) {
            String control = comprobarCodigoControl(trabajadorActual.getCodigoCuenta());
            boolean codigoErroneo = false;
            if (!"0".equals(control)) {
                if (!trabajadorActual.getCodigoCuenta().equals(control)) {
                    codigoErroneo = true;
                    xml.addErroresCCCValue(trabajadorActual);
                }
                trabajadorActual.setCodigoCuenta(control);
            }
            String iban = calcularIBAN(trabajadorActual.getCodigoCuenta(), trabajadorActual.getPaisCuenta());
            if (!"0".equals(iban)) {
                trabajadorActual.setIban(iban);
            }
            if (codigoErroneo) {
                xml.addIBAN(trabajadorActual);
            }
        } 
        xml.saveErroresCCCFile();    
    }
    
    public void generarEmail(){
        ArrayList<String> repeticionesEmail = new ArrayList<>();
        for (Trabajadorbbdd trabajadorActual : this.datosTrabajadores) {
            String correo = calcularCorreo(trabajadorActual.getNombre(), trabajadorActual.getApellido1(), trabajadorActual.getApellido2(), trabajadorActual.getEmpresas().getNombre());
            if (!"0".equals(correo)) {
                String repeticiones = calcularRepeticiones(correo, repeticionesEmail);
                repeticionesEmail.add(correo);
                correo = correo.substring(0, correo.indexOf("@"));
                correo += repeticiones + "@" + trabajadorActual.getEmpresas().getNombre() + ".com";
                trabajadorActual.setEmail(correo);
            }
        }
    }

    public void comprobarNIF_NIES() {
        ArrayList<String> dniAdded = new ArrayList<>();
      
        for (Trabajadorbbdd trabajadorActual : datosTrabajadores) {
            String nieCom = NIF_NIE(trabajadorActual.getNifnie());
            if (nieCom.equals("0")) {
                this.xml.addErroresValue(trabajadorActual);
            } else {
                if (!dniAdded.isEmpty()) {
                    if (dniAdded.contains(nieCom)) {
                        this.xml.addErroresValue(trabajadorActual);
                        trabajadorActual.setNifnie(nieCom);
                    } else {
                        trabajadorActual.setNifnie(nieCom);
                        dniAdded.add(nieCom);
                    }
                } else {
                    trabajadorActual.setNifnie(nieCom);
                    dniAdded.add(nieCom);
                }
            }  
        }
        this.xml.saveErroresFile();
    }

    public String NIF_NIE(String NIF_NIE) {
        char dniExtranjero = ' ';
        if (NIF_NIE != null) {
            if (!"".equals(NIF_NIE)) {
                switch (NIF_NIE.charAt(0)) {
                    case 'X': {
                        StringBuilder mod = new StringBuilder(NIF_NIE);
                        mod.setCharAt(0, '0');
                        NIF_NIE = mod.toString();
                        dniExtranjero = '0';
                        break;
                    }
                    case 'Y': {
                        StringBuilder mod = new StringBuilder(NIF_NIE);
                        mod.setCharAt(0, '1');
                        NIF_NIE = mod.toString();
                        dniExtranjero = '1';
                        break;
                    }
                    case 'Z': {
                        StringBuilder mod = new StringBuilder(NIF_NIE);
                        mod.setCharAt(0, '2');
                        NIF_NIE = mod.toString();
                        dniExtranjero = '2';
                        break;
                    }
                }
                int valor = Integer.valueOf(NIF_NIE.substring(0, 8));
                if (NIF_NIE.charAt(8) == letrasDNI.charAt(valor % 23)) {               
                    if (dniExtranjero != ' ') {
                        StringBuilder mod = new StringBuilder(NIF_NIE);
                        switch (dniExtranjero) {
                            case '0':
                                mod.setCharAt(0, 'X');
                                NIF_NIE = mod.toString();
                                break;
                            case '1':
                                mod.setCharAt(0, 'Y');
                                NIF_NIE = mod.toString();
                                break;
                            case '2':
                                mod.setCharAt(0, 'Z');
                                NIF_NIE = mod.toString();
                                break;
                        }
                    }
                    return NIF_NIE;
                } else {
                    StringBuilder mod = new StringBuilder(NIF_NIE);
                    if (dniExtranjero != ' ') {
                        switch (dniExtranjero) {
                            case '0':
                                mod.setCharAt(0, 'X');
                                break;
                            case '1':
                                mod.setCharAt(0, 'Y');
                                break;
                            case '2':
                                mod.setCharAt(0, 'Z');
                                break;
                        }
                    }
                    mod.setCharAt(8, letrasDNI.charAt(valor % 23));
                    return mod.toString();
                }
            }
        }
        return "0";
    }
    
     public String calcularIBAN(String control, String pais) {
        if (control != null && pais != null) {
            if (!"".equals(control)) {
                for (int i = 0; i < pais.length(); i++) {
                    control += String.valueOf(Integer.valueOf(pais.charAt(i) - 55));
                }
                control += "00";
                BigInteger inte = new BigInteger(control);
                int modulo = inte.mod(new BigInteger("97")).intValue();
                int num = 98 - modulo;
                control = control.substring(0, 20);
                String iban;
                if (num >= 10) {
                    iban = pais + String.valueOf(num) + control;
                } else {
                    iban = pais + "0" + String.valueOf(num) + control;
                }
                return iban;
            }
        }
        return "0";
    }

    public String comprobarCodigoControl(String control) {
        if (control != null) {
            if (!"".equals(control)) {
                int digito1;
                int digito2;
                String control1 = "00" + control.substring(0, 8);
                String control2 = control.substring(10, 20);
                int suma = 0;
                for (int i = 0; i < control1.length(); i++) {
                    if (i < 5) {
                        suma += Character.getNumericValue(control1.charAt(i)) * Character.getNumericValue(multiplicadorCodigo.charAt(i));
                    } else if (i == 5) {
                        suma += Character.getNumericValue(control1.charAt(i)) * 10;
                    } else {
                        suma += Character.getNumericValue(control1.charAt(i)) * Character.getNumericValue(multiplicadorCodigo.charAt(i + 1));
                    }
                }
                suma = suma % 11;
                suma = 11 - suma;
                if (suma == 11) {
                    suma = 0;
                } else if (suma == 10) {
                    suma = 1;
                }
                digito1 = suma;
                suma = 0;
                for (int i = 0; i < control2.length(); i++) {
                    if (i < 5) {
                        suma += Character.getNumericValue(control2.charAt(i)) * Character.getNumericValue(multiplicadorCodigo.charAt(i));
                    } else if (i == 5) {
                        suma += Character.getNumericValue(control2.charAt(i)) * 10;
                    } else {
                        suma += Character.getNumericValue(control2.charAt(i)) * Character.getNumericValue(multiplicadorCodigo.charAt(i + 1));
                    }
                }
                suma = suma % 11;
                suma = 11 - suma;
                if (suma == 11) {
                    suma = 0;
                } else if (suma == 10) {
                    suma = 1;
                }
                digito2 = suma;
                StringBuilder mod = new StringBuilder(control);
                mod.setCharAt(8, (char) (digito1 + '0'));
                mod.setCharAt(9, (char) (digito2 + '0'));
                return mod.toString();
            }
        }
        return "0";
    }

    public String calcularCorreo(String nombre, String apellido1, String apellido2, String empresa) {
        if (nombre != null) {
            if (!"".equals(nombre)) {
                String correo = "";
                correo += nombre.charAt(0);
                correo += apellido1.charAt(0);
                if (apellido2 != null) {
                    if (!"".equals(apellido2)) {
                        correo += apellido2.charAt(0);
                    }
                }
                return correo + "@" + empresa;
            }
        }
        return "0";
    }

    private String calcularRepeticiones(String correo, ArrayList<String> repeticionesEmail) {
        int repeticiones = Collections.frequency(repeticionesEmail, correo);
        if (repeticiones < 10) {
            return "0" + String.valueOf(repeticiones);
        } else {
            return String.valueOf(repeticiones);
        }
    }
     public Set<Nomina> calcularNominas(Trabajadorbbdd trabajador) {
        Set<Nomina> nominasT = new HashSet<>();
        if (trabajador.getNombre() != null && !"".equals(trabajador.getNombre())) {
            int mesesDif = calcularTrienios(trabajador.getFechaAlta());
            if (mesesDif > 0) {
                boolean recienContratado = isRecienContratado(trabajador.getFechaAlta());
                if (!recienContratado) {
                    Nomina nom = new Nomina();
                    nom.setTrabajadorbbdd(trabajador);
                    Categorias categoria = buscarCategoria(trabajador.getCategorias().getNombreCategoria());
                    int mesAlta = trabajador.getFechaAlta().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue();
                    int mesNomina = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue();
                    nom.setAnio(fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
                    nom.setMes(mesNomina);
                    boolean prorrateo = trabajador.isProrrateo();
                    nom.setNumeroTrienios(mesesDif / 36);
                    nom.setImporteTrienios(calculaExtraTrienios(mesesDif / 36));
                    nom.setSalarioBaseAnual(categoria.getSalarioBaseCategoria());
                    nom.setComplementoAnual(categoria.getComplementoCategoria());
                    nom.setImporteSalarioMes(nom.getSalarioBaseAnual() / 14);
                    nom.setImporteComplementoMes(nom.getComplementoAnual() / 14);
                    nom.setSeguridadSocialTrabajador(getCuotaSeguridadSocialTrabajador());
                    nom.setDesempleoTrabajador(getCuotaDesempleoTrabajador());
                    nom.setFormacionTrabajador(getCuotaFormacionTrabajador());
                    nom.setSeguridadSocialEmpresario(getCuotaSeguridadSocialEmpresario());
                    nom.setDesempleoEmpresario(getCuotaDesempleoEmpresario());
                    nom.setFogasaempresario(getCuotaFOGASAEmpresario());
                    nom.setFormacionEmpresario(getCuotaFormacionEmpresario());
                    nom.setAccidentesTrabajoEmpresario(getCuotaAccidentesTrabajoEmpresario());
                    nom.setExtra(false);
                    double meses = calculaPosibleCambioTrienio(trabajador.getFechaAlta());
                    if (!recienContratado) {
                        nom.setBrutoAnual(nom.getSalarioBaseAnual() + nom.getComplementoAnual() + (nom.getImporteTrienios() * meses) + (importeTrieniosConCambio(nom.getImporteTrienios(), mesNomina - mesAlta) * (14.0 - meses)));
                        if (prorrateo) {
                            nom = calcularNominaConProrrateo(nom);
                        } else {
                            nom = calcularNominaSinProrrateo(nom);
                        }
                    } else {
                        if (prorrateo) {
                            nom.setBrutoAnual((nom.getSalarioBaseAnual() + nom.getComplementoAnual()) / 12 * (12 - mesAlta + 1));
                            nom = calcularNominaConProrrateo(nom);
                        } else {
                            nom.setBrutoAnual(((nom.getSalarioBaseAnual() + nom.getComplementoAnual()) / 14) * (12 - mesAlta + 1) + ((nom.getSalarioBaseAnual() + nom.getComplementoAnual()) / 14) * calculaExtrasContratado(mesAlta));
                            nom = calcularNominaSinProrrateo(nom);
                        }
                    }
                    nominasT.add(nom);
                    trabajador.setCategorias(categoria);
                    if ((mesNomina == 6 || mesNomina == 12) && !trabajador.isProrrateo()) {
                        Nomina extra = calcularNominaExtra(trabajador, false);
                        nominasT.add(extra);
                    }
                }
            }
        }
        return nominasT;
    }

//    private boolean nombreBuscar(String nombuscar, String nomTrabajdor) {
//        if (nombuscar.equals(nomTrabajdor)) {
//            return true;
//        }
//        return false;
//    }
    public Categorias buscarCategoria(String categoria) {
        
        if(categoria != null){
          Categorias cat = categorias.stream().filter(o -> o.getNombreCategoria().equals(categoria)).findFirst().get();
          return cat;  
        }
        return null;
    }

    public int calcularTrienios(Date fechaAlta) {
        Period diff = Period.between(fechaAlta.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        return diff.getMonths() + (diff.getYears() * 12);
    }

    public double calculaExtraTrienios(int trienios) {
        if (trienios != 0) {
            ExtraTrienio extra = extras.stream().filter(o -> o.getNumTrienios() == trienios).findFirst().get();
            return extra.getExtra();
        }
        return 0;
    }

    public double calculaPosibleCambioTrienio(Date fechaAlta) {
        int yearAlta = fechaAlta.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        int yearNomina = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        int mesAlta = fechaAlta.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue();
        if (((yearNomina - yearAlta) % 3) == 0) {
            if (mesAlta >= 6) {
                // CALCULAMOS EL NUMERO DE MESES QUE COBRARA EL NUEVO TRIENIO Y SI ES DESPUES DE JUNIO EL CAMBIO
                return 12 - fechaAlta.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() + 1;
            } else {
                // CALCULAMOS EL NUMERO DE MESES QUE COBRARA EL NUEVO TRIENIO Y SI ES ANTES DE JUNIO EL CAMBIO
                return 12 - fechaAlta.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() + 2;
            }
        }
        return 14;
    }

    public double calculaExtrasContratado(int month) {
        double extra = 0;
        if (month < 6) {
            extra += 1;
            extra += (6 - month) / 6.0;
        } else {
            extra += (6 - (month - 6)) / 6.0;
        }
        return extra;
    }

    public double calculaExtraReduccion(int month, int monthNomina) {
        double extra = 0;
        if (monthNomina == 6) {
            extra += (6 - month) / 6.0;
            return extra;
        } else if (monthNomina == 12 && month > 6) {
            extra += (6 - (month - 6)) / 6.0;
            return extra;
        }
        return 1;
    }

    public boolean isRecienContratado(Date fechaAlta) {
        int yearAlta = fechaAlta.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        int yearNomina = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        return yearAlta - yearNomina == 0;
    }

    public double importeTrieniosConCambio(double value, double mesesDif) {
        if (value > 15) {
            if (mesesDif >= 0) {
                ExtraTrienio extra = extras.stream().filter(o -> o.getExtra() <= value).reduce((first, second) -> second).get();
                return extra.getExtra();
            } else {
                ExtraTrienio extra = extras.stream().filter(o -> o.getExtra() > value).findFirst().get();
                return extra.getExtra();
            }
        }
        return 0;
    }

    public double getCuotaSeguridadSocialTrabajador() {
        Cuota cuota = cuotas.stream().filter(o -> o.getNombreCuota().equals("Cuota obrera general TRABAJADOR")).findFirst().get();
        return cuota.getCosto() / 100;
    }

    public double getCuotaDesempleoTrabajador() {
        Cuota cuota = cuotas.stream().filter(o -> o.getNombreCuota().equals("Cuota desempleo TRABAJADOR")).findFirst().get();
        return cuota.getCosto() / 100;
    }

    public double getCuotaFormacionTrabajador() {
        Cuota cuota = cuotas.stream().filter(o -> o.getNombreCuota().equals("Cuota formación TRABAJADOR")).findFirst().get();
        return cuota.getCosto() / 100;
    }

    public double getCuotaSeguridadSocialEmpresario() {
        Cuota cuota = cuotas.stream().filter(o -> o.getNombreCuota().equals("Contingencias comunes EMPRESARIO")).findFirst().get();
        return cuota.getCosto() / 100;
    }

    public double getCuotaFOGASAEmpresario() {
        Cuota cuota = cuotas.stream().filter(o -> o.getNombreCuota().equals("Fogasa EMPRESARIO")).findFirst().get();
        return cuota.getCosto() / 100;
    }

    public double getCuotaDesempleoEmpresario() {
        Cuota cuota = cuotas.stream().filter(o -> o.getNombreCuota().equals("Desempleo EMPRESARIO")).findFirst().get();
        return cuota.getCosto() / 100;
    }

    public double getCuotaFormacionEmpresario() {
        Cuota cuota = cuotas.stream().filter(o -> o.getNombreCuota().equals("Formacion EMPRESARIO")).findFirst().get();
        return cuota.getCosto() / 100;
    }

    public double getCuotaAccidentesTrabajoEmpresario() {
        Cuota cuota = cuotas.stream().filter(o -> o.getNombreCuota().equals("Accidentes trabajo EMPRESARIO")).findFirst().get();
        return cuota.getCosto() / 100;
    }

    public double getIRPF(Double brutoAnual) {
        Retencion ret = retenciones.stream().filter(o -> o.getBrutoAnual() >= brutoAnual).findFirst().get();
        return ret.getRetencion() / 100;
    }

    public Nomina calcularNominaConProrrateo(Nomina n) {
        n.setValorProrrateo(n.getImporteSalarioMes() / 6 + n.getImporteComplementoMes() / 6 + n.getImporteTrienios() / 6);
        n.setBrutoNomina(n.getImporteSalarioMes() + n.getImporteComplementoMes() + n.getImporteTrienios() + n.getValorProrrateo());

        // TRABAJADOR
        n.setImporteSeguridadSocialTrabajador(n.getBrutoNomina() * n.getSeguridadSocialTrabajador());
        n.setImporteDesempleoTrabajador(n.getBrutoNomina() * n.getDesempleoTrabajador());
        n.setImporteFormacionTrabajador(n.getBrutoNomina() * n.getFormacionTrabajador());
        n.setIrpf(getIRPF(n.getBrutoAnual()));
        n.setImporteIrpf(n.getBrutoNomina() * n.getIrpf());
        n.setLiquidoNomina(n.getBrutoNomina() - n.getImporteSeguridadSocialTrabajador() - n.getImporteDesempleoTrabajador() - n.getImporteFormacionTrabajador() - n.getImporteIrpf());

        // EMPRESARIO
        n.setImporteSeguridadSocialEmpresario(n.getBrutoNomina() * n.getSeguridadSocialEmpresario());
        n.setImporteDesempleoEmpresario(n.getBrutoNomina() * n.getDesempleoEmpresario());
        n.setImporteFogasaempresario(n.getBrutoNomina() * n.getFogasaempresario());
        n.setImporteFormacionEmpresario(n.getBrutoNomina() * n.getFormacionEmpresario());
        n.setImporteAccidentesTrabajoEmpresario(n.getBrutoNomina() * n.getAccidentesTrabajoEmpresario());
        n.setCosteTotalEmpresario(n.getBrutoNomina() + n.getImporteAccidentesTrabajoEmpresario() + n.getImporteDesempleoEmpresario() + n.getImporteFogasaempresario() + n.getImporteFormacionEmpresario() + n.getImporteSeguridadSocialEmpresario());

        n.setBaseEmpresario(n.getBrutoNomina());
        return n;
    }

    public Nomina calcularNominaSinProrrateo(Nomina n) {

        n.setValorProrrateo(0.0);
        n.setBrutoNomina(n.getImporteSalarioMes() + n.getImporteComplementoMes() + n.getImporteTrienios() + n.getValorProrrateo());

        // TRABAJADOR
        double sueldo12Meses = (n.getBrutoNomina() / 6) + n.getBrutoNomina();
        n.setImporteSeguridadSocialTrabajador(sueldo12Meses * n.getSeguridadSocialTrabajador());
        n.setImporteDesempleoTrabajador(sueldo12Meses * n.getDesempleoTrabajador());
        n.setImporteFormacionTrabajador(sueldo12Meses * n.getFormacionTrabajador());
        n.setIrpf(getIRPF(n.getBrutoAnual()));
        n.setImporteIrpf(n.getBrutoNomina() * n.getIrpf());
        n.setLiquidoNomina(n.getBrutoNomina() - n.getImporteSeguridadSocialTrabajador() - n.getImporteDesempleoTrabajador() - n.getImporteFormacionTrabajador() - n.getImporteIrpf());

        // EMPRESARIO
        n.setImporteSeguridadSocialEmpresario(sueldo12Meses * n.getSeguridadSocialEmpresario());
        n.setImporteDesempleoEmpresario(sueldo12Meses * n.getDesempleoEmpresario());
        n.setImporteFogasaempresario(sueldo12Meses * n.getFogasaempresario());
        n.setImporteFormacionEmpresario(sueldo12Meses * n.getFormacionEmpresario());
        n.setImporteAccidentesTrabajoEmpresario(sueldo12Meses * n.getAccidentesTrabajoEmpresario());
        n.setCosteTotalEmpresario(n.getBrutoNomina() + n.getImporteAccidentesTrabajoEmpresario() + n.getImporteDesempleoEmpresario() + n.getImporteFogasaempresario() + n.getImporteFormacionEmpresario() + n.getImporteSeguridadSocialEmpresario());

        n.setBaseEmpresario(sueldo12Meses);
        return n;
    }

    public Nomina calcularNominaExtra(Trabajadorbbdd trabajador, boolean isNuevo) {
        int mesesDif = calcularTrienios(trabajador.getFechaAlta());
        Nomina nom = new Nomina();
        nom.setTrabajadorbbdd(trabajador);
        Categorias categoria = buscarCategoria(trabajador.getCategorias().getNombreCategoria());
        int mesAlta = trabajador.getFechaAlta().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue();
        int mesNomina = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue();
        nom.setAnio(fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
        nom.setMes(mesNomina);
        nom.setNumeroTrienios(mesesDif / 36);
        nom.setImporteTrienios(calculaExtraTrienios(mesesDif / 36));
        nom.setSalarioBaseAnual(categoria.getSalarioBaseCategoria());
        nom.setComplementoAnual(categoria.getComplementoCategoria());
        nom.setExtra(true);
        double meses = calculaPosibleCambioTrienio(trabajador.getFechaAlta());
        if (!isNuevo) {
            nom.setBrutoAnual(nom.getSalarioBaseAnual() + nom.getComplementoAnual() + (nom.getImporteTrienios() * meses) + (importeTrieniosConCambio(nom.getImporteTrienios(), mesNomina - mesAlta) * (14.0 - meses)));
            nom.setImporteSalarioMes(nom.getSalarioBaseAnual() / 14);
            nom.setImporteComplementoMes(nom.getComplementoAnual() / 14);
        } else {
            nom.setBrutoAnual(((nom.getSalarioBaseAnual() + nom.getComplementoAnual()) / 14) * (12 - mesAlta + 1) + ((nom.getSalarioBaseAnual() + nom.getComplementoAnual()) / 14) * calculaExtrasContratado(mesAlta));
            nom.setImporteSalarioMes((nom.getSalarioBaseAnual() / 14) * calculaExtraReduccion(mesAlta, mesNomina));
            nom.setImporteComplementoMes(nom.getComplementoAnual() / 14 * calculaExtraReduccion(mesAlta, mesNomina));
        }

        nom.setValorProrrateo(0.0);
        nom.setBrutoNomina(nom.getImporteSalarioMes() + nom.getImporteComplementoMes() + nom.getImporteTrienios() + nom.getValorProrrateo());
        // TRABAJADOR
        nom.setSeguridadSocialTrabajador(0.0);
        nom.setImporteSeguridadSocialTrabajador(0.0);
        nom.setDesempleoTrabajador(0.0);
        nom.setImporteDesempleoTrabajador(0.0);
        nom.setFormacionTrabajador(0.0);
        nom.setImporteFormacionTrabajador(0.0);
        nom.setIrpf(getIRPF(nom.getBrutoAnual()));
        nom.setImporteIrpf(nom.getBrutoNomina() * nom.getIrpf());
        nom.setLiquidoNomina(nom.getBrutoNomina() - nom.getImporteSeguridadSocialTrabajador() - nom.getImporteDesempleoTrabajador() - nom.getImporteFormacionTrabajador() - nom.getImporteIrpf());

        // EMPRESARIO
        nom.setSeguridadSocialEmpresario(0.0);
        nom.setImporteSeguridadSocialEmpresario(0.0);
        nom.setDesempleoEmpresario(0.0);
        nom.setImporteDesempleoEmpresario(0.0);
        nom.setFogasaempresario(0.0);
        nom.setImporteFogasaempresario(0.0);
        nom.setFormacionEmpresario(0.0);
        nom.setImporteFormacionEmpresario(0.0);
        nom.setAccidentesTrabajoEmpresario(0.0);
        nom.setImporteAccidentesTrabajoEmpresario(0.0);
        nom.setCosteTotalEmpresario(nom.getBrutoNomina() + nom.getImporteAccidentesTrabajoEmpresario() + nom.getImporteDesempleoEmpresario() + nom.getImporteFogasaempresario() + nom.getImporteFormacionEmpresario() + nom.getImporteSeguridadSocialEmpresario());
        nom.setBaseEmpresario(0.0);
        return nom;
    }
}
