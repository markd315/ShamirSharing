

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Main extends javax.swing.JFrame {
    
	private static final long serialVersionUID = 1L;
	/** Creates new form */
	private ShamirAPI db;
    private javax.swing.JComboBox<String> nameSelector;
    private javax.swing.JButton queryButton;
    private javax.swing.JTextArea queryResponse;
    private javax.swing.JButton updateButton;
    private javax.swing.JButton insertButton;
    private javax.swing.JLabel debugLabel;
    
    //To connect queryResponse to flowerIndex:
    private String[] queryContents;
  
    //For updates 
    private javax.swing.JLabel flowerLabel;
    private javax.swing.JTextArea flowerGenus;
    private javax.swing.JTextArea flowerSpecies;
    private javax.swing.JTextArea flowerComname;
   
    //For inserts
    private javax.swing.JLabel sightingLabel;
    private javax.swing.JTextArea sightingPerson;
    private javax.swing.JTextArea sightingLocation;
    private javax.swing.JTextArea sightingDate;
    
    public Main() {
    	db = new DatabaseAPI();
    	debugLabel = new javax.swing.JLabel();
        nameSelector = new javax.swing.JComboBox<String>();
        queryButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        insertButton = new javax.swing.JButton();
        queryResponse = new javax.swing.JTextArea();
        flowerLabel = new javax.swing.JLabel();
        sightingLabel = new javax.swing.JLabel();
        flowerGenus = new javax.swing.JTextArea();
        flowerSpecies = new javax.swing.JTextArea();
        flowerComname = new javax.swing.JTextArea();
        sightingPerson = new javax.swing.JTextArea();
        sightingLocation = new javax.swing.JTextArea();
        sightingDate = new javax.swing.JTextArea();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SQLite frontend");

        queryButton.setText("Query");
        queryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryButtonActionPerformed(evt);
            }
        });
        
        insertButton.setText("Insert");
        insertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertButtonActionPerformed(evt);
            }
        });
        
        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        debugLabel.setText("Run a query!");
        sightingLabel.setText("Insert sighting for this flower: person, location, YYYY-MM-DD");
        flowerLabel.setText("Update this flower: genus, species, new comname");
        
        
        updateNameSelector();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(debugLabel))
                    .addGroup(layout.createSequentialGroup()
                            .addComponent(flowerLabel))
                    .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(flowerGenus)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(flowerSpecies)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(flowerComname)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(updateButton))
                    .addGroup(layout.createSequentialGroup()
                    		.addComponent(sightingLabel))
                    .addGroup(layout.createSequentialGroup()
                    		.addComponent(sightingPerson)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(sightingLocation)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(sightingDate)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(insertButton))
                	.addGroup(layout.createSequentialGroup()
                        .addComponent(queryButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(queryResponse)
                .addContainerGap(27, Short.MAX_VALUE)))
        ));
        
        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {queryButton, nameSelector});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                	.addComponent(debugLabel))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(flowerLabel))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(flowerGenus)
                        .addComponent(flowerSpecies)
                        .addComponent(flowerComname)
                        .addComponent(updateButton))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(sightingLabel))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(sightingPerson)
                        .addComponent(sightingLocation)
                        .addComponent(sightingDate)
                        .addComponent(insertButton))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(queryResponse))        
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(queryButton))
                
        ));
        pack();
    }
    
    private void updateNameSelector() {
    	nameSelector.removeAllItems();
    	String[] rets = db.listFlowers();
        for(String single : rets) {
        	nameSelector.addItem(single);
        }
	}

	private void queryButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	queryContents = db.listSightings((String) nameSelector.getSelectedItem());
    	String lineDelimited = "";
    	for(String s : queryContents) {
    		lineDelimited+=s;
    		lineDelimited+="\n";
    	}
    	queryResponse.setText(lineDelimited);
    	debugLabel.setText("Repopulated list: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()));
    }
    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	debugLabel.setText(db.update(flowerGenus.getText(), flowerSpecies.getText(), flowerComname.getText(), (String) nameSelector.getSelectedItem()));
    	updateNameSelector();
    }
    private void insertButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	debugLabel.setText(db.insert((String) nameSelector.getSelectedItem(), sightingPerson.getText(), sightingLocation.getText(), sightingDate.getText()));
    }
    
    
    
    
    public static void main(String args[]) {
    	java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
   
}