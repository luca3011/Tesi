package it.unibo.tesi.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import java.io.FileOutputStream;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;



public class XlsUtility {

    private File file;

    private ArrayList<File> files;

    public XlsUtility(File file) throws FileNotFoundException {
        this.file = file;

        this.files = new ArrayList<>();

        if (file.isDirectory()) {
            for (final File fileEntry : file.listFiles()) {
                if (!fileEntry.isDirectory()) {

                    if (fileEntry.getName().endsWith(".xls")) {
                        this.files.add(fileEntry);
                    }
                }
            }
        } else if (file.isFile()) {
            if (file.getName().endsWith(".xls")) {
                this.files.add(file);
            }
        } else {
            System.err.println("File " + file.getName() + " non valido");
            throw new FileNotFoundException();
        }

    }

    public ArrayList<File> getFiles() {
        return files;
    }

    // trasforma le righe di tutti i file excel in oggetti RigaExcel
    public ArrayList<RigaExcel> leggiRigheExcel() {

        ArrayList<RigaExcel> result = new ArrayList<RigaExcel>();
        ArrayList<RigaExcel> temp;

        RigaExcel riga;

        for (File filexls : this.files) {

            try {

                temp = new ArrayList<>();

                FileInputStream fis = new FileInputStream(filexls);
                Workbook workbook = new HSSFWorkbook(fis);

                // prendo il primo foglio del file
                Sheet sheet = workbook.getSheetAt(0);

                String ArticleCode;
                Date StartTestTime;
                String Result;

                //variabile per saltare la prima riga
                boolean intestazione = true;

                for (Row row : sheet) {

                    if (!intestazione) {
                        ArticleCode = row.getCell(2).getStringCellValue();
                        StartTestTime = row.getCell(3).getDateCellValue();
                        Result = row.getCell(27).getStringCellValue();

                        riga = new RigaExcel(ArticleCode, StartTestTime, Result);
                        result.add(riga);

                    }

                    intestazione = false;

                }

                workbook.close();

                result.addAll(temp);

            } catch (Exception e) {
                System.out.println("Errore in lettura file " + filexls.getName() + " : " + e.getLocalizedMessage());
            }

        }

        return result;
    }

    //elimina le righe contenenti un certo ODP
    public int removeOdPdaXls(String numero_odp) {
		
        int result = 0;


        for (File fileXls : this.files) {
            
            try {

                FileInputStream fis = new FileInputStream(fileXls);
                Workbook workbookOrig = new HSSFWorkbook(fis);
                Workbook workbookDest = new HSSFWorkbook();
    
                // prendo il primo foglio del file origine
                Sheet sheetOrig = workbookOrig.getSheetAt(0);
    
                // creo un foglio nel file destinazione
                Sheet sheetDest = workbookDest.createSheet();
    
                Row nuovaRiga;
                int righeCopiate = 0;
    
                for (Row row : sheetOrig) {
    
                    System.out.println(row.getCell(2));
    
                    if (row.getCell(2) != null) {
    
                        if (!row.getCell(2).getStringCellValue().startsWith(numero_odp) || row.getRowNum() == 0) {
                            
                            nuovaRiga = sheetDest.createRow(righeCopiate);
    
                            for (Cell cell : row) {
    
                                int i = cell.getColumnIndex();
    
                                Cell oldCell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
    
                                if (oldCell == null) {
                                    nuovaRiga.createCell(i).setCellValue("");
                                } else if (oldCell.getCellType() == CellType.NUMERIC) {
                                    nuovaRiga.createCell(i).setCellValue(oldCell.getDateCellValue());
                                } else if (oldCell.getCellType() == CellType.BOOLEAN) {
                                    nuovaRiga.createCell(i).setCellValue(oldCell.getBooleanCellValue());
                                } else {
                                    nuovaRiga.createCell(i).setCellValue(oldCell.getStringCellValue());
                                }
    
                            }
    
                            righeCopiate++;
    
                        }
                    }
                }
    
                workbookOrig.close();
    
                FileOutputStream fileOut = null;
                try {
                    fileOut = new FileOutputStream(fileXls);
                    workbookDest.write(fileOut);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    fileOut.close();
                }
    
                workbookDest.close();

                result = result + righeCopiate;
    
            } catch (Exception e) {
                System.out.println("Errore eliminazione righe dell'Odp: " + numero_odp + ", " + e.getLocalizedMessage());
            }
    
            
            
        }
        
        return result;

	}

    //elimina le righe degli odp nella lista passata come argomento
    public int removeOdPdaXls(ArrayList<String> lista_odp){

        int result = 0;

        for (String numeroOdp : lista_odp) {
            result += removeOdPdaXls(numeroOdp);
        }

        return result;

    }

    public File getFile() {
        return file;
    }

}