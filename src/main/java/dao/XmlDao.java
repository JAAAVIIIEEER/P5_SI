/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import modelo.Nomina;
import modelo.Trabajadorbbdd;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 *
 * @author jp
 */
public class XmlDao {
    private Document documentErorres;
    private Element rootErrores;
    private Document documentErroresCCC;
    private Element rootErroresCCC;
    private Document documentNominas;
    private Element rootNominas;
    private Element cuenta;

    public XmlDao(String fecha){
        this.createErroresFile();
        this.createErroresCCCFile();
        this.createNominasFile(fecha);
    }

    public void createErroresFile() {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            documentErorres = documentBuilder.newDocument();
            documentErorres.setXmlVersion("1.0");
            documentErorres.setXmlStandalone(true);
            rootErrores = documentErorres.createElement("Trabajadores");
            documentErorres.appendChild(rootErrores);
        } catch (ParserConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void addErroresValue(Trabajadorbbdd trabajador) {
            Element trab = documentErorres.createElement("Trabajador");
            rootErrores.appendChild(trab);
            trab.setAttribute("id", String.valueOf(trabajador.getIdTrabajador()));
            
            if (trabajador.getNombre() == null) {
                Element nombre = documentErorres.createElement("Nombre");
                nombre.appendChild(documentErorres.createTextNode(""));
                trab.appendChild(nombre);
            } else {
                Element nombre = documentErorres.createElement("Nombre");
                nombre.appendChild(documentErorres.createTextNode(trabajador.getNombre()));
                trab.appendChild(nombre);
            }
            if (trabajador.getApellido1()== null) {
                Element apellido1 = documentErorres.createElement("PrimerApellido");
                apellido1.appendChild(documentErorres.createTextNode(""));
                trab.appendChild(apellido1);
            } else {
                Element apellido1 = documentErorres.createElement("PrimerApellido");
                apellido1.appendChild(documentErorres.createTextNode(trabajador.getApellido1()));
                trab.appendChild(apellido1);
            }
            if (trabajador.getApellido2() == null) {
                Element apellido2 = documentErorres.createElement("SegundoApellido");
                apellido2.appendChild(documentErorres.createTextNode(""));
                trab.appendChild(apellido2);
            } else {
                Element apellido2 = documentErorres.createElement("SegundoApellido");
                apellido2.appendChild(documentErorres.createTextNode(trabajador.getApellido2()));
                trab.appendChild(apellido2);
            }
            
            if (trabajador.getEmpresas().getNombre() == null) {
                Element empresa = documentErorres.createElement("Empresa");
                empresa.appendChild(documentErorres.createTextNode(""));
                trab.appendChild(empresa);
            } else {
                Element empresa = documentErorres.createElement("Empresa");
                empresa.appendChild(documentErorres.createTextNode(trabajador.getEmpresas().getNombre()));
                trab.appendChild(empresa);
            }
            if (trabajador.getCategorias() == null) {
                Element categoria = documentErorres.createElement("Categoria");
                categoria.appendChild(documentErorres.createTextNode(""));
                trab.appendChild(categoria);
            } else {
                Element categoria = documentErorres.createElement("Categoria");
                categoria.appendChild(documentErorres.createTextNode(trabajador.getCategorias().getNombreCategoria()));
                trab.appendChild(categoria);
            }
    }

    public void saveErroresFile() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource domSource = new DOMSource(documentErorres);
            StreamResult streamResult = new StreamResult(new File("resources/errores.xml"));
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void createErroresCCCFile() {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            documentErroresCCC = documentBuilder.newDocument();
            documentErroresCCC.setXmlVersion("1.0");
            documentErroresCCC.setXmlStandalone(true);
            rootErroresCCC = documentErroresCCC.createElement("Cuentas");
            documentErroresCCC.appendChild(rootErroresCCC);
        } catch (ParserConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void addErroresCCCValue(Trabajadorbbdd trabajador) {
        if (trabajador.getNombre() != null) {
            cuenta = documentErroresCCC.createElement("Cuenta");
            rootErroresCCC.appendChild(cuenta);
            cuenta.setAttribute("id", String.valueOf(trabajador.getIdTrabajador()));

            Element nombre = documentErroresCCC.createElement("Nombre");
            nombre.appendChild(documentErroresCCC.createTextNode(trabajador.getNombre()));
            cuenta.appendChild(nombre);

            Element apellidos = documentErroresCCC.createElement("Apellidos");
            apellidos.appendChild(documentErroresCCC.createTextNode(trabajador.getApellido1() + " " + trabajador.getApellido2()));
            cuenta.appendChild(apellidos);

            Element empresa = documentErroresCCC.createElement("Empresa");
            empresa.appendChild(documentErroresCCC.createTextNode(trabajador.getEmpresas().getNombre()));
            cuenta.appendChild(empresa);

            Element ccc = documentErroresCCC.createElement("CCCErroneo");
            ccc.appendChild(documentErroresCCC.createTextNode(trabajador.getCodigoCuenta()));
            cuenta.appendChild(ccc);
        }
    }

    public void addIBAN(Trabajadorbbdd trabajador) {
        Element iban = documentErroresCCC.createElement("IBANCorrecto");
        iban.appendChild(documentErroresCCC.createTextNode(trabajador.getIban()));
        cuenta.appendChild(iban);
    }

    public void saveErroresCCCFile() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource domSource = new DOMSource(documentErroresCCC);
            StreamResult streamResult = new StreamResult(new File("resources/erroresCCC.xml"));
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void createNominasFile(String fecha) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            documentNominas = documentBuilder.newDocument();
            documentNominas.setXmlVersion("1.0");
            documentNominas.setXmlStandalone(true);
            rootNominas = documentNominas.createElement("Nominas");
            documentNominas.appendChild(rootNominas);
            rootNominas.setAttribute("fechaNomina", fecha);
        } catch (ParserConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void addNominaValue(Nomina n) {
            Element trab = documentNominas.createElement("Nomina");
            rootNominas.appendChild(trab);
            trab.setAttribute("idNomina", String.valueOf(n.getIdNomina()));
            
            if(n.isExtra()) {
                Element ide = documentNominas.createElement("Extra");
                ide.appendChild(documentNominas.createTextNode("S"));
                trab.appendChild(ide);
            } else {
                Element ide = documentNominas.createElement("Extra");
                ide.appendChild(documentNominas.createTextNode("N"));
                trab.appendChild(ide);
            }
            if (n.getTrabajadorbbdd().getIdTrabajador() == null) {
                Element ide = documentNominas.createElement("idFilaExcel");
                ide.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(ide);
            } else {
                Element ide = documentNominas.createElement("idFilaExcel");
                ide.appendChild(documentNominas.createTextNode(String.valueOf(n.getTrabajadorbbdd().getIdTrabajador())));
                trab.appendChild(ide);
            }
            if (n.getTrabajadorbbdd().getNombre() == null) {
                Element nombre = documentNominas.createElement("Nombre");
                nombre.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(nombre);
            } else {
                Element nombre = documentNominas.createElement("Nombre");
                nombre.appendChild(documentNominas.createTextNode(n.getTrabajadorbbdd().getNombre()));
                trab.appendChild(nombre);
            }
            if (n.getTrabajadorbbdd().getNifnie() == null) {
                Element nif = documentNominas.createElement("NIF");
                nif.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(nif);
            } else {
                Element nif = documentNominas.createElement("NIF");
                nif.appendChild(documentNominas.createTextNode(n.getTrabajadorbbdd().getNifnie()));
                trab.appendChild(nif);
            }
            if (n.getTrabajadorbbdd().getIban() == null) {
                Element iban = documentNominas.createElement("IBAN");
                iban.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(iban);
            } else {
                Element iban = documentNominas.createElement("IBAN");
                iban.appendChild(documentNominas.createTextNode(n.getTrabajadorbbdd().getIban()));
                trab.appendChild(iban);
            }
            if (n.getTrabajadorbbdd().getCategorias().getNombreCategoria() == null) {
                Element categorias = documentNominas.createElement("Categoria");
                categorias.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(categorias);
            } else {
                Element categorias = documentNominas.createElement("Categoria");
                categorias.appendChild(documentNominas.createTextNode(n.getTrabajadorbbdd().getCategorias().getNombreCategoria()));
                trab.appendChild(categorias);
            }
            if (n.getBrutoAnual() == null) {
                Element brutoAnual = documentNominas.createElement("BrutoAnual");
                brutoAnual.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(brutoAnual);
            } else {
                Element nombre = documentNominas.createElement("BrutoAnual");
                nombre.appendChild(documentNominas.createTextNode(String.valueOf(n.getBrutoAnual())));
                trab.appendChild(nombre);
            }
            if (n.getIrpf() == null) {
                Element nombre = documentNominas.createElement("ImporteIrpf");
                nombre.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(nombre);
            } else {
                Element nombre = documentNominas.createElement("ImporteIrpf");
                nombre.appendChild(documentNominas.createTextNode(String.valueOf(n.getIrpf())));
                trab.appendChild(nombre);
            }
            if (n.getBaseEmpresario() == null) {
                Element nombre = documentNominas.createElement("BaseEmpresario");
                nombre.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(nombre);
            } else {
                Element nombre = documentNominas.createElement("BaseEmpresario");
                nombre.appendChild(documentNominas.createTextNode(String.valueOf(n.getBaseEmpresario())));
                trab.appendChild(nombre);
            }
            if (n.getBrutoNomina() == null) {
                Element nombre = documentNominas.createElement("BrutoNomina");
                nombre.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(nombre);
            } else {
                Element nombre = documentNominas.createElement("BrutoNomina");
                nombre.appendChild(documentNominas.createTextNode(String.valueOf(n.getBrutoNomina())));
                trab.appendChild(nombre);
            }
            if (n.getLiquidoNomina() == null) {
                Element nombre = documentNominas.createElement("LiquidoNomina");
                nombre.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(nombre);
            } else {
                Element nombre = documentNominas.createElement("LiquidoNomina");
                nombre.appendChild(documentNominas.createTextNode(String.valueOf(n.getLiquidoNomina())));
                trab.appendChild(nombre);
            }
            if (n.getCosteTotalEmpresario() == null) {
                Element nombre = documentNominas.createElement("CosteTotalEmpresario");
                nombre.appendChild(documentNominas.createTextNode(""));
                trab.appendChild(nombre);
            } else {
                Element nombre = documentNominas.createElement("CosteTotalEmpresario");
                nombre.appendChild(documentNominas.createTextNode(String.valueOf(n.getCosteTotalEmpresario())));
                trab.appendChild(nombre);
            }
    }
    
    public void saveNominasFile() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource domSource = new DOMSource(documentNominas);
            StreamResult streamResult = new StreamResult(new File("resources/nominas.xml"));
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
    }
}
