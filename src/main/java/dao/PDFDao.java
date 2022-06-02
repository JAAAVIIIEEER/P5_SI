/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Nomina;

/**
 *
 * @author jp
 */
public class PDFDao {
    public void createPDF(Nomina n) {
        DecimalFormat df = new DecimalFormat("00.00");
        StringBuilder ruta = new StringBuilder("./resources/nominas/");
        File theDir = new File("./resources/nominas/");
        if (!theDir.exists()){
            theDir.mkdirs();
        }
        ruta.append(n.getTrabajadorbbdd().getNifnie());
        ruta.append(n.getTrabajadorbbdd().getNombre());
        ruta.append(n.getTrabajadorbbdd().getApellido1());
        ruta.append(n.getTrabajadorbbdd().getApellido2());
        ruta.append(resolverMes(n.getMes()));
        ruta.append(n.getAnio());
        if (n.isExtra()) {
            ruta.append("EXTRA");
        }
        ruta.append(".pdf");
        PdfWriter writer;
        try {
            writer = new PdfWriter(new File(ruta.toString()));
            PdfDocument pdfDoc = new PdfDocument(writer);
            try (Document doc = new Document(pdfDoc, PageSize.LETTER)) {
                Table tabla1 = new Table(2);
                tabla1.setWidth(500);
                Paragraph nom = new Paragraph(n.getTrabajadorbbdd().getEmpresas().getNombre());
                Paragraph cif = new Paragraph("CIF: " + n.getTrabajadorbbdd().getEmpresas().getCif());
                Paragraph dir1 = new Paragraph("Avenida de la facultad - 6");
                Paragraph dir2 = new Paragraph("24001 León");
                Cell cell1 = new Cell();
                cell1.setBorder(new SolidBorder(1));
                cell1.setWidth(250);
                cell1.setTextAlignment(TextAlignment.CENTER);
                cell1.add(nom);
                cell1.add(cif);
                cell1.add(dir1);
                cell1.add(dir2);
                tabla1.addCell(cell1);
                Cell cell2 = new Cell();
                cell2.setBorder(Border.NO_BORDER);
                cell2.setPadding(10);
                cell2.setTextAlignment(TextAlignment.RIGHT);
                cell2.add(new Paragraph("IBAN: " + n.getTrabajadorbbdd().getIban()));
                cell2.add(new Paragraph("Bruto anual: " + n.getBrutoAnual()));
                cell2.add(new Paragraph("Categoría: " + n.getTrabajadorbbdd().getCategorias().getNombreCategoria()));

                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String fecha = format.format(n.getTrabajadorbbdd().getFechaAlta());

                cell2.add(new Paragraph("Fecha de alta: " + fecha));
                tabla1.addCell(cell2);

                Table tabla2 = new Table(2);
                tabla2.setWidth(500);
                tabla2.setMarginTop(25);
                Image img = new Image(ImageDataFactory.create("./resources/gestoria.png"));
                img.setWidth(100);
                img.setHeight(101);
                img.setBorder(Border.NO_BORDER);
                img.setPadding(10);
                img.setMarginRight(50);
                Cell cell3 = new Cell();
                cell3.add(img);
                cell3.setBorder(Border.NO_BORDER);
                cell3.setTextAlignment(TextAlignment.LEFT);
                cell3.setPaddingLeft(75);
                cell3.setPaddingTop(0);
                cell3.setWidth(250);
                tabla2.addCell(cell3);

                // DESTINATARIO
                Paragraph dest = new Paragraph("Destinatario:");
                Paragraph nomBre = new Paragraph(n.getTrabajadorbbdd().getNombre() + " " + n.getTrabajadorbbdd().getApellido1() + " " + n.getTrabajadorbbdd().getApellido2());
                Paragraph dni = new Paragraph(n.getTrabajadorbbdd().getNifnie());
                Paragraph direc2 = new Paragraph("Avenida de la facultad");
                Paragraph direc1 = new Paragraph("24001 León");
                Cell cell4 = new Cell();
                cell4.setBorder(new SolidBorder(1));
                cell4.setWidth(250);
                cell4.setTextAlignment(TextAlignment.RIGHT);
                dest.setTextAlignment(TextAlignment.LEFT);
                dest.setBold();
                cell4.setPadding(10);
                cell4.add(dest);
                cell4.add(nomBre);
                cell4.add(dni);
                cell4.add(direc2);
                cell4.add(direc1);
                tabla2.addCell(cell4);

                // NOMBRE NOMINA
                Paragraph tituloNom = new Paragraph("Nómina: " + tituloNomina(n.isExtra(), n.getMes()) + n.getAnio());
                tituloNom.setBold();
                tituloNom.setTextAlignment(TextAlignment.CENTER);
                tituloNom.setItalic();
                tituloNom.setMarginTop(20);
                tituloNom.setMarginTop(10);
                tituloNom.setFontSize(12);

                // TABLA 3
                Table tabla3 = new Table(UnitValue.createPercentArray(5)).setWidth(500);
                tabla3.setTextAlignment(TextAlignment.CENTER);

                Cell conceptos = new Cell();
                conceptos.add(new Paragraph("Conceptos"));
                conceptos.setBorder(Border.NO_BORDER);
                tabla3.addCell(conceptos);

                Cell cantidad = new Cell();
                cantidad.add(new Paragraph("Cantidad"));
                cantidad.setBorder(Border.NO_BORDER);
                tabla3.addCell(cantidad);

                Cell unitario = new Cell();
                unitario.add(new Paragraph("Imp. Unitario"));
                unitario.setBorder(Border.NO_BORDER);
                tabla3.addCell(unitario);

                Cell devengo = new Cell();
                devengo.add(new Paragraph("Devengo"));
                devengo.setBorder(Border.NO_BORDER);
                tabla3.addCell(devengo);

                Cell deduccion = new Cell();
                deduccion.add(new Paragraph("Deducción"));
                deduccion.setBorder(Border.NO_BORDER);
                tabla3.addCell(deduccion);

                //SALARIO BASE
                Cell salarioBase = new Cell();
                salarioBase.add(new Paragraph("Salario base"));
                salarioBase.setBorder(Border.NO_BORDER);
                tabla3.addCell(salarioBase);

                Cell dias1 = new Cell();
                dias1.add(new Paragraph("30 días"));
                dias1.setBorder(Border.NO_BORDER);
                tabla3.addCell(dias1);

                Cell importeSalario = new Cell();
                importeSalario.add(new Paragraph(String.valueOf(df.format(n.getImporteSalarioMes() / 30))));
                importeSalario.setBorder(Border.NO_BORDER);
                tabla3.addCell(importeSalario);

                Cell devengoSalario = new Cell();
                devengoSalario.add(new Paragraph(String.valueOf(df.format(n.getImporteSalarioMes()))));
                devengoSalario.setBorder(Border.NO_BORDER);
                tabla3.addCell(devengoSalario);

                Cell empty1 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty1);

                // PRORRATEO
                Cell prorrateo = new Cell();
                prorrateo.add(new Paragraph("Prorrateo"));
                prorrateo.setBorder(Border.NO_BORDER);
                tabla3.addCell(prorrateo);

                Cell dias2 = new Cell();
                dias2.add(new Paragraph("30 días"));
                dias2.setBorder(Border.NO_BORDER);
                tabla3.addCell(dias2);

                Cell importeProrrateo = new Cell();
                importeProrrateo.add(new Paragraph(String.valueOf(df.format(n.getValorProrrateo() / 30))));
                importeProrrateo.setBorder(Border.NO_BORDER);
                tabla3.addCell(importeProrrateo);

                Cell devengoProrrateo = new Cell();
                devengoProrrateo.add(new Paragraph(String.valueOf(df.format(n.getValorProrrateo()))));
                devengoProrrateo.setBorder(Border.NO_BORDER);
                tabla3.addCell(devengoProrrateo);

                Cell empty2 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty2);

                // COMPLEMENTOS
                Cell complemento = new Cell();
                complemento.add(new Paragraph("Complemento"));
                complemento.setBorder(Border.NO_BORDER);
                tabla3.addCell(complemento);

                Cell dias3 = new Cell();
                dias3.add(new Paragraph("30 días"));
                dias3.setBorder(Border.NO_BORDER);
                tabla3.addCell(dias3);

                Cell importeComplemento = new Cell();
                importeComplemento.add(new Paragraph(String.valueOf(df.format(n.getImporteComplementoMes() / 30))));
                importeComplemento.setBorder(Border.NO_BORDER);
                tabla3.addCell(importeComplemento);

                Cell devengoComplemento = new Cell();
                devengoComplemento.add(new Paragraph(String.valueOf(df.format(n.getImporteComplementoMes()))));
                devengoComplemento.setBorder(Border.NO_BORDER);
                tabla3.addCell(devengoComplemento);

                Cell empty3 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty3);

                // ANTIGUEDAD
                Cell antiguedad = new Cell();
                antiguedad.add(new Paragraph("Antigüedad"));
                antiguedad.setBorder(Border.NO_BORDER);
                tabla3.addCell(antiguedad);

                Cell dias4 = new Cell();
                dias4.add(new Paragraph(n.getNumeroTrienios() + " Trienios"));
                dias4.setBorder(Border.NO_BORDER);
                tabla3.addCell(dias4);

                Cell importeTrienio = new Cell();
                importeTrienio.add(new Paragraph(String.valueOf(df.format(n.getImporteTrienios() / n.getNumeroTrienios()))));
                importeTrienio.setBorder(Border.NO_BORDER);
                tabla3.addCell(importeTrienio);

                Cell devengoTrienio = new Cell();
                devengoTrienio.add(new Paragraph(String.valueOf(df.format(n.getImporteTrienios()))));
                devengoTrienio.setBorder(Border.NO_BORDER);
                tabla3.addCell(devengoTrienio);

                Cell empty4 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty4);

                // CONTINGENCIAS GENERALES TRABAJADOR
                Cell contingencias = new Cell();
                contingencias.add(new Paragraph("Contingencias\u00A0generales"));
                contingencias.setBorder(Border.NO_BORDER);
                tabla3.addCell(contingencias);

                Cell porcentajeContingencias = new Cell();
                porcentajeContingencias.add(new Paragraph(df.format(n.getSeguridadSocialTrabajador() * 100) + "%\u00A0de\u00A0" + df.format(n.getBaseEmpresario())));
                porcentajeContingencias.setBorder(Border.NO_BORDER);
                tabla3.addCell(porcentajeContingencias);

                Cell empty5 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty5);

                Cell empty6 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty6);

                Cell deduccionContingencias = new Cell();
                deduccionContingencias.add(new Paragraph(String.valueOf(df.format(n.getImporteSeguridadSocialTrabajador()))));
                deduccionContingencias.setBorder(Border.NO_BORDER);
                tabla3.addCell(deduccionContingencias);

                // DESEMPLEO TRABAJADOR
                Cell desempleoTrabajador = new Cell();
                desempleoTrabajador.add(new Paragraph("Desempleo"));
                desempleoTrabajador.setBorder(Border.NO_BORDER);
                tabla3.addCell(desempleoTrabajador);

                Cell porcentajeDesempleo = new Cell();
                porcentajeDesempleo.add(new Paragraph(df.format(n.getDesempleoTrabajador() * 100) + "%\u00A0de\u00A0" + df.format(n.getBaseEmpresario())));
                porcentajeDesempleo.setBorder(Border.NO_BORDER);
                tabla3.addCell(porcentajeDesempleo);

                Cell empty7 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty7);

                Cell empty8 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty8);

                Cell deduccionDesempleo = new Cell();
                deduccionDesempleo.add(new Paragraph(String.valueOf(df.format(n.getImporteDesempleoTrabajador()))));
                deduccionDesempleo.setBorder(Border.NO_BORDER);
                tabla3.addCell(deduccionDesempleo);

                // FORMACION TRABAJADOR
                Cell formacionTrabajador = new Cell();
                formacionTrabajador.add(new Paragraph("Cuota formación"));
                formacionTrabajador.setBorder(Border.NO_BORDER);
                tabla3.addCell(formacionTrabajador);

                Cell porcentajeFormacion = new Cell();
                porcentajeFormacion.add(new Paragraph(df.format(n.getFormacionTrabajador() * 100) + "%\u00A0de\u00A0" + df.format(n.getBaseEmpresario())));
                porcentajeFormacion.setBorder(Border.NO_BORDER);
                tabla3.addCell(porcentajeFormacion);

                Cell empty9 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty9);

                Cell empty10 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty10);

                Cell deduccionFormacion = new Cell();
                deduccionFormacion.add(new Paragraph(String.valueOf(df.format(n.getImporteFormacionTrabajador()))));
                deduccionFormacion.setBorder(Border.NO_BORDER);
                tabla3.addCell(deduccionFormacion);

                // IRPF
                Cell irpf = new Cell();
                irpf.add(new Paragraph("IRPF"));
                irpf.setBorder(Border.NO_BORDER);
                tabla3.addCell(irpf);

                Cell porcentajeIRPF = new Cell();
                porcentajeIRPF.add(new Paragraph(df.format(n.getIrpf() * 100) + "%\u00A0de\u00A0" + df.format(n.getBrutoNomina())));
                porcentajeIRPF.setBorder(Border.NO_BORDER);
                tabla3.addCell(porcentajeIRPF);

                Cell empty11 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty11);

                Cell empty12 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty12);

                Cell deduccionIRPF = new Cell();
                deduccionIRPF.add(new Paragraph(String.valueOf(df.format(n.getImporteIrpf()))));
                deduccionIRPF.setBorder(Border.NO_BORDER);
                tabla3.addCell(deduccionIRPF);

                // TOTAL DEDUCCIONES
                Cell totalDeducciones = new Cell();
                totalDeducciones.add(new Paragraph("Total deducciones"));
                totalDeducciones.setBorder(Border.NO_BORDER);
                tabla3.addCell(totalDeducciones);

                Cell empty13 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty13);

                Cell empty14 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty14);

                Cell empty15 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty15);

                Cell sumaDeducciones = new Cell();
                sumaDeducciones.add(new Paragraph(String.valueOf(df.format(n.getImporteIrpf() + n.getImporteDesempleoTrabajador() + n.getImporteFormacionTrabajador() + n.getImporteSeguridadSocialTrabajador()))));
                sumaDeducciones.setBorder(Border.NO_BORDER);
                tabla3.addCell(sumaDeducciones);

                // TOTAL DEVENGOS
                Cell totalDevengos = new Cell();
                totalDevengos.add(new Paragraph("Total devengos"));
                totalDevengos.setBorder(Border.NO_BORDER);
                tabla3.addCell(totalDevengos);

                Cell empty16 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty16);

                Cell empty17 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty17);

                Cell sumaDevengos = new Cell();
                sumaDevengos.add(new Paragraph(String.valueOf(df.format(n.getImporteSalarioMes() + n.getImporteComplementoMes() + n.getImporteTrienios() + n.getValorProrrateo()))));
                sumaDevengos.setBorder(Border.NO_BORDER);
                tabla3.addCell(sumaDevengos);

                Cell empty18 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty18);

                // TOTAL DEVENGOS
                Cell empty19 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty19);

                Cell empty20 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty20);

                Cell liquido = new Cell();
                liquido.add(new Paragraph("Liquido a percibir"));
                liquido.setBorder(Border.NO_BORDER);
                tabla3.addCell(liquido);

                Cell empty21 = new Cell().add(new Paragraph()).setBorder(Border.NO_BORDER);
                tabla3.addCell(empty21);

                Cell liquidoNum = new Cell();
                liquidoNum.add(new Paragraph(String.valueOf(df.format(n.getLiquidoNomina()))));
                liquidoNum.setBorder(Border.NO_BORDER);
                tabla3.addCell(liquidoNum);

                for (int i = 0; i < tabla3.getNumberOfColumns(); i++) {
                    tabla3.getCell(0, i).setBorderBottom(new SolidBorder(1));
                    tabla3.getCell(0, i).setBorderTop(new SolidBorder(1));
                    tabla3.getCell(8, i).setBorderBottom(new SolidBorder(1));
                    tabla3.getCell(10, i).setBorderBottom(new SolidBorder(1));
                }

                tabla3.setMarginBottom(18);

                for (int i = 0; i < tabla3.getNumberOfRows(); i++) {
                    tabla3.getCell(i, 0).setTextAlignment(TextAlignment.LEFT);
                    tabla3.getCell(i, 0).setPaddingRight(15);
                    tabla3.getCell(i, 4).setPaddingLeft(25);
                }

                tabla3.setFontSize(10);

                // TABLA 4
                Table tabla4 = new Table(UnitValue.createPercentArray(2)).setWidth(500);
                tabla4.setFontColor(ColorConstants.GRAY);

                // BASE EMPRESARIO
                Cell baseEmpresario = new Cell();
                baseEmpresario.add(new Paragraph("Calculo empresario: BASE"));
                baseEmpresario.setBorder(Border.NO_BORDER);
                tabla4.addCell(baseEmpresario);

                Cell baseEmpresarioNum = new Cell();
                baseEmpresarioNum.add(new Paragraph(String.valueOf(df.format(n.getBaseEmpresario()))));
                baseEmpresarioNum.setBorder(Border.NO_BORDER);
                tabla4.addCell(baseEmpresarioNum);

                // CONTIGENCIAS EMPRESARIO
                Cell contingenciasEmpresario = new Cell();
                contingenciasEmpresario.add(new Paragraph("Contingencias comunes empresario " + df.format(n.getSeguridadSocialEmpresario() * 100) + "%"));
                contingenciasEmpresario.setBorder(Border.NO_BORDER);
                tabla4.addCell(contingenciasEmpresario);

                Cell contingenciasNum = new Cell();
                contingenciasNum.add(new Paragraph(String.valueOf(df.format(n.getImporteSeguridadSocialEmpresario()))));
                contingenciasNum.setBorder(Border.NO_BORDER);
                tabla4.addCell(contingenciasNum);

                // DESEMPLEO EMPRESARIO
                Cell desempleoEmpresario = new Cell();
                desempleoEmpresario.add(new Paragraph("Desempleo " + df.format(n.getDesempleoEmpresario() * 100) + "%"));
                desempleoEmpresario.setBorder(Border.NO_BORDER);
                tabla4.addCell(desempleoEmpresario);

                Cell desempleoNum = new Cell();
                desempleoNum.add(new Paragraph(String.valueOf(df.format(n.getImporteDesempleoEmpresario()))));
                desempleoNum.setBorder(Border.NO_BORDER);
                tabla4.addCell(desempleoNum);
                
                // FORMACION EMPRESARIO
                Cell formacionEmpresario = new Cell();
                formacionEmpresario.add(new Paragraph("Formación " + df.format(n.getFormacionEmpresario() * 100) + "%"));
                formacionEmpresario.setBorder(Border.NO_BORDER);
                tabla4.addCell(formacionEmpresario);

                Cell formacionNum = new Cell();
                formacionNum.add(new Paragraph(String.valueOf(df.format(n.getImporteFormacionEmpresario()))));
                formacionNum.setBorder(Border.NO_BORDER);
                tabla4.addCell(formacionNum);
                
                // ACCIDENTES TRABAJO EMPRESARIO
                Cell accidentesEmpresario = new Cell();
                accidentesEmpresario.add(new Paragraph("Accidentes de trabajo " + df.format(n.getAccidentesTrabajoEmpresario() * 100) + "%"));
                accidentesEmpresario.setBorder(Border.NO_BORDER);
                tabla4.addCell(accidentesEmpresario);

                Cell accidentesNum = new Cell();
                accidentesNum.add(new Paragraph(String.valueOf(df.format(n.getImporteAccidentesTrabajoEmpresario()))));
                accidentesNum.setBorder(Border.NO_BORDER);
                tabla4.addCell(accidentesNum);
                
                // FOGASA EMPRESARIO
                Cell fogasaEmpresario = new Cell();
                fogasaEmpresario.add(new Paragraph("FOGASA " + df.format(n.getFogasaempresario() * 100) + "%"));
                fogasaEmpresario.setBorder(Border.NO_BORDER);
                tabla4.addCell(fogasaEmpresario);

                Cell fogasaNum = new Cell();
                fogasaNum.add(new Paragraph(String.valueOf(df.format(n.getImporteFogasaempresario()))));
                fogasaNum.setBorder(Border.NO_BORDER);
                tabla4.addCell(fogasaNum);
                
                // TOTAL EMPRESARIO
                Cell totalEmpresario = new Cell();
                totalEmpresario.add(new Paragraph("Total empresario"));
                totalEmpresario.setBorder(Border.NO_BORDER);
                tabla4.addCell(totalEmpresario);

                Cell totalNum = new Cell();
                totalNum.add(new Paragraph(String.valueOf(df.format(n.getImporteFogasaempresario()+n.getImporteAccidentesTrabajoEmpresario()+n.getImporteFormacionEmpresario()+n.getImporteDesempleoEmpresario()+n.getImporteSeguridadSocialEmpresario()))));
                totalNum.setBorder(Border.NO_BORDER); 
                tabla4.addCell(totalNum);
                
                // COSTE TRABAJADOR
                Cell totalTrabajador = new Cell();
                totalTrabajador.add(new Paragraph("COSTE TOTAL TRABAJADOR:"));
                totalTrabajador.setBorder(Border.NO_BORDER);
                totalTrabajador.setBorderTop(new SolidBorder(3));
                totalTrabajador.setBorderLeft(new SolidBorder(3));
                totalTrabajador.setBorderBottom(new SolidBorder(3));
                totalTrabajador.setFontColor(ColorConstants.RED);
                tabla4.addCell(totalTrabajador);

                Cell totalTrabajadorNum = new Cell();
                totalTrabajadorNum.add(new Paragraph(String.valueOf(df.format(n.getCosteTotalEmpresario()))));
                totalTrabajadorNum.setBorder(Border.NO_BORDER);
                totalTrabajadorNum.setBorderTop(new SolidBorder(3));
                totalTrabajadorNum.setBorderRight(new SolidBorder(3));
                totalTrabajadorNum.setBorderBottom(new SolidBorder(3));
                totalTrabajadorNum.setFontColor(ColorConstants.RED);
                tabla4.addCell(totalTrabajadorNum);
                
                tabla4.setFontSize(10);

                for (int i = 0; i < tabla4.getNumberOfColumns(); i++) {
                    tabla4.getCell(0, i).setBorderBottom(new SolidBorder(ColorConstants.GRAY, 1));
                    tabla4.getCell(5, i).setBorderBottom(new SolidBorder(ColorConstants.GRAY, 1));
                    tabla4.getCell(0, i).setBorderTop(new SolidBorder(ColorConstants.GRAY, 1));
                    tabla4.getCell(1, i).setPaddingTop(15);
                    tabla4.getCell(6, i).setPaddingBottom(15);
                }

                for (int i = 0; i < tabla4.getNumberOfRows(); i++) {
                    tabla4.getCell(i, 1).setTextAlignment(TextAlignment.RIGHT);
                }

                doc.add(tabla1);
                doc.add(tabla2);
                doc.add(tituloNom);
                doc.add(tabla3);
                doc.add(tabla4);

                doc.close();
            } catch (IOException ex) {
                Logger.getLogger(PDFDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PDFDao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String resolverMes(int mes) {
        switch (mes) {
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";

        }
        return "";
    }

    public String tituloNomina(boolean extra, int mes) {
        StringBuilder nombre = new StringBuilder();
        if (extra) {
            nombre.append("Extra de ");
            nombre.append(resolverMes(mes).toLowerCase());
        } else {
            nombre.append(resolverMes(mes));
        }
        nombre.append(" de ");
        return nombre.toString();
    }
    
    public void crearPDF(){
        StringBuilder ruta = new StringBuilder("./resources/nominas/");
        File theDir = new File("./resources/nominas/");
        if (!theDir.exists()){
            theDir.mkdirs();
        }
        ruta.append("PRACTICAFINAL-JAVIERPRADANOS");
        ruta.append(".pdf");
        PdfWriter writer;
        try {
            writer = new PdfWriter(new File(ruta.toString()));
            PdfDocument pdfDoc = new PdfDocument(writer);
            try (Document doc = new Document(pdfDoc, PageSize.LETTER)) {
          
                // NOMBRE NOMINA
                Paragraph tituloNom = new Paragraph("SISTEMAS DE INFORMACIÓN II");
                tituloNom.setBold();
                tituloNom.setTextAlignment(TextAlignment.CENTER);
                tituloNom.setMarginTop(20);
                tituloNom.setMarginTop(10);
                tituloNom.setFontSize(20);
                Paragraph nombre = new Paragraph("Alumno : Javier Prádanos Fombellida");
                nombre.setTextAlignment(TextAlignment.CENTER);
                nombre.setMarginTop(10);
                nombre.setFontSize(12);
                
                
                Table tabla4 = new Table(UnitValue.createPercentArray(2)).setWidth(500);
                tabla4.setFontColor(ColorConstants.GRAY);

                Cell maxName = new Cell();
                maxName.add(new Paragraph("Categoria con mas Trabajadores : "));
                maxName.setBorder(Border.NO_BORDER);
                tabla4.addCell(maxName);
                Cell maxValue = new Cell();
                maxValue.add(new Paragraph("Operador - 72"));
                maxValue.setBorder(Border.NO_BORDER);
                tabla4.addCell(maxValue);
                Cell minName = new Cell();
                minName.add(new Paragraph("Categoria con menos Trabajadores :"));
                minName.setBorder(Border.NO_BORDER);
                tabla4.addCell(minName);
                Cell minValue = new Cell();
                minValue.add(new Paragraph("Calefactor - 2"));
                minValue.setBorder(Border.NO_BORDER);
                tabla4.addCell(minValue);
                tabla4.setMarginTop(10);
                doc.add(tituloNom);
                                doc.add(nombre);
doc.add(tabla4);
                doc.close();
            }        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PDFDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
