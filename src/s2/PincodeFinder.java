import javax.swing.*;    
import java.awt.event.*;    
import java.io.*;    
import java.util.Iterator;
import java.io.File;  
import java.io.FileInputStream;  
import java.util.Iterator;  
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook; 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class PincodeFinder extends JFrame implements ActionListener{    
JMenuBar mb;    
JMenu file;    
JMenuItem open;    
JTextArea ta;    
PincodeFinder(){    
open=new JMenuItem("Open File");    
open.addActionListener(this);            
file=new JMenu("File");    
file.add(open);             
mb=new JMenuBar();    
mb.setBounds(0,0,800,20);    
mb.add(file);              
ta=new JTextArea(800,800);    
ta.setBounds(0,20,800,800);              
add(mb);    
add(ta);              
}    
public void actionPerformed(ActionEvent e) {    
if(e.getSource()==open){    
    JFileChooser fc=new JFileChooser();    
    int i=fc.showOpenDialog(this);    
    if(i==JFileChooser.APPROVE_OPTION){    
        File file=fc.getSelectedFile();    
        String filepath=file.getPath();  
        
        try  
            {  
            //JFileChooser fc = new JFileChooser();
            //File file = fc.getSelectedFile();//creating a new file instance  
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file  
            //creating Workbook instance that refers to .xlsx file  
            
            XSSFWorkbook wb = new XSSFWorkbook(fis);   
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object  
            Iterator<Row> itr = sheet.iterator(); //iterating over excel file  
            while (itr.hasNext())                 
            {  
            Row row = itr.next();  
            Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column  
            while (cellIterator.hasNext())   
            {  
            Cell cell = cellIterator.next();  
            
            switch (cell.getCellType())               
            {  
            case Cell.CELL_TYPE_STRING:    //field that represents string cell type  
            System.out.print("Excel does not contain valid pincode in row: " + cell.getRowIndex());            
            break;  
            case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type  
                int intvalue =(int)cell.getNumericCellValue();
                String[] locDetails = getLocationDetails(String.valueOf(intvalue));
                for (int j = 0; j < locDetails.length; j++)
                {
                    System.out.print(locDetails[j] + "  ");
                    Cell tempcell = row.createCell(j+1);
                    tempcell.setCellValue(locDetails[j]);
                }            
            break;  
            default:  
            }  
            }  
            System.out.println("");  
            }  
            FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.close();
            }  
            catch(Exception et)  
            {  
            et.printStackTrace();  
            }   
    }    
}    
}   
public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

 private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    String str = sb.toString();
    return str.substring(1, str.length() - 1);
  }
 
public static String[] getLocationDetails(String Pincode) throws IOException, JSONException{
    String[] st = new String[4];
    JSONObject json = readJsonFromUrl("https://api.postalpincode.in/pincode/" + Pincode);
    JSONArray arr = json.getJSONArray("PostOffice");    
        for (int i = 0; i < arr.length(); i++)
        {
            if(st[0] == null){
                st[0] = arr.getJSONObject(i).getString("Name");
            }else{
                st[0] = st[0] + ",\n" + arr.getJSONObject(i).getString("Name");
            }
            st[1] = arr.getJSONObject(i).getString("District");
            st[2] = arr.getJSONObject(i).getString("State");
            st[3] = arr.getJSONObject(i).getString("Country");
        }
    //System.out.println(json.toString());
   // System.out.println(json.get("id"));
    
    return st;
       
}
public static void main(String[] args) {    
    PincodeFinder om=new PincodeFinder();    
             om.setSize(500,500);    
             om.setLayout(null);    
             om.setVisible(true);    
             om.setDefaultCloseOperation(EXIT_ON_CLOSE);    
}    
}  
