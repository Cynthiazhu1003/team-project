/*
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
*/
package frontend;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.Border;

/**
 *
 * @author benja
 */
public class HomePageView extends javax.swing.JFrame {
    
    // --- Card identifiers ---
    private static final String CARD_HOME     = "cardHome";
    private static final String CARD_TRANS    = "cardTransaction";
    private static final String CARD_BUDGET   = "cardBudget";
    private static final String CARD_REPORT   = "cardReport";
    private static final String CARD_IMPORT   = "cardImport";
    private static final String CARD_ADD_TRANS   = "cardAddTransaction";
    private static final String CARD_ADD_BUDGET = "cardAddBudget";
    private DashedRoundBorder dropIdleBorder;
    private DashedRoundBorder dropActiveBorder;
    private JLabel dropLabel;  // text inside the drop zone

    // --- Helper method to switch cards ---
    private void showCard(String cardName) {
        java.awt.CardLayout layout = (java.awt.CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, cardName);
    }
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(HomePageView.class.getName());

    /**
     * Creates new form HomePage
     */
    public HomePageView() {
        initComponents();
        // Register each card with its name
        mainPanel.add(cardHome,        CARD_HOME);
        mainPanel.add(cardTransaction, CARD_TRANS);
        mainPanel.add(cardBudget,      CARD_BUDGET);
        mainPanel.add(cardReport,      CARD_REPORT);
        mainPanel.add(cardImport,      CARD_IMPORT);
        mainPanel.add(cardAddTransaction,CARD_ADD_TRANS);
        mainPanel.add(cardAddBudget, CARD_ADD_BUDGET);

        // Optional: show a default screen when program starts
        showCard(CARD_HOME);
        
        // ---- Drop box styling + DnD ----
        styleImportDropBox();   // dashed rounded border + label
        enableCsvDrop();        // drag-and-drop CSV handler + hover highlight
    }
    
    /** Style jPanel11 like the mock: white card, dashed rounded border, title + subtitle. */
    private void styleImportDropBox() {
        // PANEL (drop zone)
        jPanel11.setOpaque(true);
        jPanel11.setBackground(Color.WHITE);
        jPanel11.setLayout(new BorderLayout());

        // make the panel bigger than the label’s text
        jPanel11.setPreferredSize(new java.awt.Dimension(360, 180));
        jPanel11.setMinimumSize(new java.awt.Dimension(320, 160));

        dropIdleBorder   = new DashedRoundBorder(new Color(60,60,60), 3f, 16f, 10f, 6f, 12);
        dropActiveBorder = new DashedRoundBorder(new Color(0,0,0),    3f, 16f, 10f, 6f, 12);
        jPanel11.setBorder(dropIdleBorder); // <-- border on the PANEL

        // LABEL (content)
        String html = "Drop CSV here";
        dropLabel = new JLabel(html, SwingConstants.CENTER);
        // inner padding for the text; this does NOT affect the panel border
        dropLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(16,16,16,16));
        jPanel11.add(dropLabel, BorderLayout.CENTER);
    }

    /** Enable drag-and-drop of files; accept only .csv; highlight border on hover. */
    @SuppressWarnings("unchecked")
    private void enableCsvDrop() {
        new DropTarget(jPanel11, new DropTargetListener() {
            @Override public void dragEnter(DropTargetDragEvent e) {
                if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    e.acceptDrag(DnDConstants.ACTION_COPY);
                    jPanel11.setBorder(dropActiveBorder);
                    jPanel11.repaint();
                } else {
                    e.rejectDrag();
                }
            }
            @Override public void dragOver(DropTargetDragEvent e) { /* no-op */ }
            @Override public void dropActionChanged(DropTargetDragEvent e) { /* no-op */ }
            @Override public void dragExit(DropTargetEvent e) {
                jPanel11.setBorder(dropIdleBorder);
                jPanel11.repaint();
            }
            @Override public void drop(DropTargetDropEvent e) {
                try {
                    e.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> files = (List<File>) e.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);

                    boolean gotCsv = false;
                    for (File f : files) {
                        if (f.getName().toLowerCase().endsWith(".csv")) {
                            gotCsv = true;
                            // TODO: store/use file path for your Import button
                            // e.g., this.lastDroppedCsv = f;
                            dropLabel.setText("<html><div style='text-align:center;'>"
                                    + "<div style='font-size:16px; font-weight:700; margin-bottom:6px;'>"
                                    + f.getName() + "</div>"
                                    + "<div style='font-size:12px; color:#666;'>Ready to import.</div>"
                                    + "</div></html>");
                            break;
                        }
                    }
                    if (!gotCsv) {
                        dropLabel.setText("<html><div style='text-align:center;'>"
                                + "<div style='font-size:16px; font-weight:700; margin-bottom:6px;'>Not a CSV</div>"
                                + "<div style='font-size:12px; color:#666;'>Please drop a .csv file.</div>"
                                + "</div></html>");
                    }
                } catch (Exception ex) {
                    logger.log(java.util.logging.Level.SEVERE, "Drop failed", ex);
                } finally {
                    jPanel11.setBorder(dropIdleBorder);
                    jPanel11.repaint();
                    e.dropComplete(true);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        sidebarPanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        projectLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        transactionButton = new javax.swing.JButton();
        budgetButton = new javax.swing.JButton();
        reportButton = new javax.swing.JButton();
        homeButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        cardHome = new javax.swing.JPanel();
        cardTransaction = new javax.swing.JPanel();
        transactionHeader = new javax.swing.JPanel();
        transactionHeaderHeader = new javax.swing.JPanel();
        transactionHeaderTitle = new javax.swing.JPanel();
        transactionLabel = new javax.swing.JLabel();
        transactionHeaderButton = new javax.swing.JPanel();
        addTransactionButton = new javax.swing.JButton();
        transactionFilterPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterCategoryBox = new javax.swing.JComboBox<>();
        transactionScrollPane = new javax.swing.JScrollPane();
        transactionTable = new javax.swing.JTable();
        cardBudget = new javax.swing.JPanel();
        budgetHeaderPanel = new javax.swing.JPanel();
        budgetHeader = new javax.swing.JLabel();
        addBudgetButton = new javax.swing.JButton();
        budgetScrollPane = new javax.swing.JScrollPane();
        budgetTable = new javax.swing.JTable();
        cardCharts = new javax.swing.JPanel();
        cardReport = new javax.swing.JPanel();
        reportHeaderPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        cardImport = new javax.swing.JPanel();
        importHeaderPanel = new javax.swing.JPanel();
        importLabel = new javax.swing.JLabel();
        importBodyPanel = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        importFileButton = new javax.swing.JButton();
        cancelImportButton = new javax.swing.JButton();
        cardAddTransaction = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        cancelAddTransactionButton = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        cardAddBudget = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jComboBox8 = new javax.swing.JComboBox<>();
        jPanel12 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 112, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel12.setText("jLabel12");

        jLabel15.setText("jLabel15");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sidebarPanel.setBackground(new java.awt.Color(100, 100, 100));
        sidebarPanel.setLayout(new java.awt.BorderLayout());

        titlePanel.setBackground(new java.awt.Color(153, 153, 153));
        titlePanel.setPreferredSize(new java.awt.Dimension(100, 50));

        projectLabel.setFont(new java.awt.Font("Old English Text MT", 1, 16)); // NOI18N
        projectLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        projectLabel.setText("PFT");

        javax.swing.GroupLayout titlePanelLayout = new javax.swing.GroupLayout(titlePanel);
        titlePanel.setLayout(titlePanelLayout);
        titlePanelLayout.setHorizontalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(projectLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
        );
        titlePanelLayout.setVerticalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(projectLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        sidebarPanel.add(titlePanel, java.awt.BorderLayout.PAGE_START);

        buttonPanel.setBackground(new java.awt.Color(100, 100, 100));

        transactionButton.setBackground(new java.awt.Color(100, 100, 100));
        transactionButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        transactionButton.setText("Transactions");
        transactionButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        transactionButton.setContentAreaFilled(false);
        transactionButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        transactionButton.setOpaque(true);
        transactionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                transactionButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                transactionButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                transactionButtonMouseExited(evt);
            }
        });
        transactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transactionButtonActionPerformed(evt);
            }
        });

        budgetButton.setBackground(new java.awt.Color(100, 100, 100));
        budgetButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        budgetButton.setText("Budgets");
        budgetButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        budgetButton.setContentAreaFilled(false);
        budgetButton.setOpaque(true);
        budgetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                budgetButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                budgetButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                budgetButtonMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                budgetButtonMousePressed(evt);
            }
        });
        budgetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                budgetButtonActionPerformed(evt);
            }
        });

        reportButton.setBackground(new java.awt.Color(100, 100, 100));
        reportButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        reportButton.setText("Reports");
        reportButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        reportButton.setContentAreaFilled(false);
        reportButton.setOpaque(true);
        reportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reportButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                reportButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                reportButtonMouseExited(evt);
            }
        });
        reportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportButtonActionPerformed(evt);
            }
        });

        homeButton.setBackground(new java.awt.Color(100, 100, 100));
        homeButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        homeButton.setText("Home");
        homeButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        homeButton.setContentAreaFilled(false);
        homeButton.setOpaque(true);
        homeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButtonMouseExited(evt);
            }
        });
        homeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeButtonActionPerformed(evt);
            }
        });

        importButton.setBackground(new java.awt.Color(100, 100, 100));
        importButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        importButton.setText("Import");
        importButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        importButton.setContentAreaFilled(false);
        importButton.setOpaque(true);
        importButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                importButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                importButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                importButtonMouseExited(evt);
            }
        });
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(transactionButton, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
            .addComponent(budgetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(reportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(importButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(homeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(homeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(transactionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(budgetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(importButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(320, Short.MAX_VALUE))
        );

        sidebarPanel.add(buttonPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(sidebarPanel, java.awt.BorderLayout.LINE_START);

        mainPanel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout cardHomeLayout = new javax.swing.GroupLayout(cardHome);
        cardHome.setLayout(cardHomeLayout);
        cardHomeLayout.setHorizontalGroup(
            cardHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 551, Short.MAX_VALUE)
        );
        cardHomeLayout.setVerticalGroup(
            cardHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 604, Short.MAX_VALUE)
        );

        mainPanel.add(cardHome, "card2");

        cardTransaction.setLayout(new java.awt.BorderLayout());

        transactionHeader.setPreferredSize(new java.awt.Dimension(445, 80));
        transactionHeader.setLayout(new java.awt.BorderLayout());

        transactionHeaderHeader.setPreferredSize(new java.awt.Dimension(445, 50));
        transactionHeaderHeader.setLayout(new java.awt.BorderLayout());

        transactionLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        transactionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        transactionLabel.setText("Transactions");

        javax.swing.GroupLayout transactionHeaderTitleLayout = new javax.swing.GroupLayout(transactionHeaderTitle);
        transactionHeaderTitle.setLayout(transactionHeaderTitleLayout);
        transactionHeaderTitleLayout.setHorizontalGroup(
            transactionHeaderTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transactionHeaderTitleLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(transactionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        transactionHeaderTitleLayout.setVerticalGroup(
            transactionHeaderTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(transactionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        transactionHeaderHeader.add(transactionHeaderTitle, java.awt.BorderLayout.LINE_START);

        addTransactionButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addTransactionButton.setText("Add Transaction [+]");
        addTransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTransactionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout transactionHeaderButtonLayout = new javax.swing.GroupLayout(transactionHeaderButton);
        transactionHeaderButton.setLayout(transactionHeaderButtonLayout);
        transactionHeaderButtonLayout.setHorizontalGroup(
            transactionHeaderButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transactionHeaderButtonLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(addTransactionButton)
                .addContainerGap(59, Short.MAX_VALUE))
        );
        transactionHeaderButtonLayout.setVerticalGroup(
            transactionHeaderButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transactionHeaderButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addTransactionButton, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        transactionHeaderHeader.add(transactionHeaderButton, java.awt.BorderLayout.CENTER);

        transactionHeader.add(transactionHeaderHeader, java.awt.BorderLayout.PAGE_START);

        transactionFilterPanel.setMinimumSize(new java.awt.Dimension(100, 30));
        transactionFilterPanel.setPreferredSize(new java.awt.Dimension(445, 50));

        filterLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        filterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        filterLabel.setText("Filter by Category:");

        filterCategoryBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Category...", "Dining", "Leisure", "Gifts", "School" }));

        javax.swing.GroupLayout transactionFilterPanelLayout = new javax.swing.GroupLayout(transactionFilterPanel);
        transactionFilterPanel.setLayout(transactionFilterPanelLayout);
        transactionFilterPanelLayout.setHorizontalGroup(
            transactionFilterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transactionFilterPanelLayout.createSequentialGroup()
                .addGap(159, 159, 159)
                .addComponent(filterLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterCategoryBox, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(147, Short.MAX_VALUE))
        );
        transactionFilterPanelLayout.setVerticalGroup(
            transactionFilterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transactionFilterPanelLayout.createSequentialGroup()
                .addGroup(transactionFilterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterLabel)
                    .addComponent(filterCategoryBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        transactionHeader.add(transactionFilterPanel, java.awt.BorderLayout.CENTER);

        cardTransaction.add(transactionHeader, java.awt.BorderLayout.PAGE_START);

        transactionScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        transactionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Amount", "Company", "Category", "Actions"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        transactionScrollPane.setViewportView(transactionTable);
        if (transactionTable.getColumnModel().getColumnCount() > 0) {
            transactionTable.getColumnModel().getColumn(0).setResizable(false);
            transactionTable.getColumnModel().getColumn(1).setResizable(false);
            transactionTable.getColumnModel().getColumn(2).setResizable(false);
            transactionTable.getColumnModel().getColumn(3).setResizable(false);
            transactionTable.getColumnModel().getColumn(4).setResizable(false);
        }

        cardTransaction.add(transactionScrollPane, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardTransaction, "card3");

        cardBudget.setLayout(new java.awt.BorderLayout());

        budgetHeaderPanel.setPreferredSize(new java.awt.Dimension(445, 50));

        budgetHeader.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        budgetHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        budgetHeader.setText("Monthly Budget");

        addBudgetButton.setText("Add Budget [+]");
        addBudgetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBudgetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout budgetHeaderPanelLayout = new javax.swing.GroupLayout(budgetHeaderPanel);
        budgetHeaderPanel.setLayout(budgetHeaderPanelLayout);
        budgetHeaderPanelLayout.setHorizontalGroup(
            budgetHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(budgetHeaderPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(budgetHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
                .addComponent(addBudgetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );
        budgetHeaderPanelLayout.setVerticalGroup(
            budgetHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, budgetHeaderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(budgetHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addBudgetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(budgetHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addContainerGap())
        );

        cardBudget.add(budgetHeaderPanel, java.awt.BorderLayout.PAGE_START);

        budgetScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        budgetTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Category", "Budget", "Spent", "Remaining", "Action"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        budgetScrollPane.setViewportView(budgetTable);
        if (budgetTable.getColumnModel().getColumnCount() > 0) {
            budgetTable.getColumnModel().getColumn(0).setResizable(false);
            budgetTable.getColumnModel().getColumn(1).setResizable(false);
            budgetTable.getColumnModel().getColumn(2).setResizable(false);
            budgetTable.getColumnModel().getColumn(3).setResizable(false);
            budgetTable.getColumnModel().getColumn(4).setResizable(false);
        }

        cardBudget.add(budgetScrollPane, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardBudget, "card4");

        cardCharts.setLayout(new java.awt.BorderLayout());
        mainPanel.add(cardCharts, "card5");

        cardReport.setLayout(new java.awt.BorderLayout());

        reportHeaderPanel.setMinimumSize(new java.awt.Dimension(100, 150));
        reportHeaderPanel.setPreferredSize(new java.awt.Dimension(443, 150));
        reportHeaderPanel.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(200, 200, 200));
        jPanel2.setPreferredSize(new java.awt.Dimension(443, 50));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Reports");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );

        reportHeaderPanel.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel4.setPreferredSize(new java.awt.Dimension(462, 50));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel5.setPreferredSize(new java.awt.Dimension(462, 38));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel7.setPreferredSize(new java.awt.Dimension(225, 38));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Time Period:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "7 days", "14 days", "1 month", "2 months", "6 months", "1 year" }));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel7, java.awt.BorderLayout.LINE_START);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel3.setText("Select Category:");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Dining", "Leisure", "Work", "School" }));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(76, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel8, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel6.setPreferredSize(new java.awt.Dimension(462, 62));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jButton1.setText("Generate Report");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(172, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(167, 167, 167))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.add(jPanel6, java.awt.BorderLayout.CENTER);

        reportHeaderPanel.add(jPanel4, java.awt.BorderLayout.CENTER);

        cardReport.add(reportHeaderPanel, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Report", "Action"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        cardReport.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardReport, "card6");

        cardImport.setLayout(new java.awt.BorderLayout());

        importHeaderPanel.setBackground(new java.awt.Color(200, 200, 200));
        importHeaderPanel.setPreferredSize(new java.awt.Dimension(462, 50));

        importLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        importLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        importLabel.setText("Import");

        javax.swing.GroupLayout importHeaderPanelLayout = new javax.swing.GroupLayout(importHeaderPanel);
        importHeaderPanel.setLayout(importHeaderPanelLayout);
        importHeaderPanelLayout.setHorizontalGroup(
            importHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(importLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
        );
        importHeaderPanelLayout.setVerticalGroup(
            importHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importHeaderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(importLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        cardImport.add(importHeaderPanel, java.awt.BorderLayout.PAGE_START);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        jPanel11.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 230, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 118, Short.MAX_VALUE)
        );

        importFileButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        importFileButton.setText("Import");

        cancelImportButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelImportButton.setText("Cancel");

        javax.swing.GroupLayout importBodyPanelLayout = new javax.swing.GroupLayout(importBodyPanel);
        importBodyPanel.setLayout(importBodyPanelLayout);
        importBodyPanelLayout.setHorizontalGroup(
            importBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importBodyPanelLayout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(importBodyPanelLayout.createSequentialGroup()
                .addContainerGap(150, Short.MAX_VALUE)
                .addComponent(cancelImportButton)
                .addGap(79, 79, 79)
                .addComponent(importFileButton)
                .addGap(171, 171, 171))
        );
        importBodyPanelLayout.setVerticalGroup(
            importBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importBodyPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(importBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importFileButton)
                    .addComponent(cancelImportButton))
                .addContainerGap(370, Short.MAX_VALUE))
        );

        cardImport.add(importBodyPanel, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardImport, "card8");

        cardAddTransaction.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(200, 200, 200));
        jPanel1.setPreferredSize(new java.awt.Dimension(551, 50));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Add Transaction");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(251, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        cardAddTransaction.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Date:");

        jLabel6.setText("Year:");

        jLabel7.setText("Month:");

        jLabel8.setText("Day:");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2025", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016" }));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        jComboBox5.setMaximumRowCount(32);
        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Amount:");

        jTextField1.setText("0.00");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Store Name:");

        jTextField2.setText("Store");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Category:");

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select...", "Dining", "Leisure", "Gifts", "Work" }));

        cancelAddTransactionButton.setBackground(new java.awt.Color(255, 0, 0));
        cancelAddTransactionButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelAddTransactionButton.setText("Cancel");
        cancelAddTransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAddTransactionButtonActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 255, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Add Transaction");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addGap(5, 5, 5)
                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cancelAddTransactionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(77, 77, 77)
                        .addComponent(jButton3)))
                .addContainerGap(112, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelAddTransactionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(361, Short.MAX_VALUE))
        );

        cardAddTransaction.add(jPanel9, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardAddTransaction, "card8");

        cardAddBudget.setLayout(new java.awt.BorderLayout());

        jPanel10.setBackground(new java.awt.Color(200, 200, 200));
        jPanel10.setPreferredSize(new java.awt.Dimension(551, 50));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel14.setText("Set Budget Limit");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(171, 171, 171)
                .addComponent(jLabel14)
                .addContainerGap(191, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addGap(16, 16, 16))
        );

        cardAddBudget.add(jPanel10, java.awt.BorderLayout.PAGE_START);

        jPanel13.setPreferredSize(new java.awt.Dimension(551, 250));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Category:");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Amount:");

        jTextField3.setText("0.00");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 0, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 255, 0));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Set Limit");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(73, 73, 73)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(69, 69, 69)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(99, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(414, Short.MAX_VALUE))
        );

        cardAddBudget.add(jPanel13, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardAddBudget, "card9");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(240, Short.MAX_VALUE)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(211, 211, 211))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(409, Short.MAX_VALUE))
        );

        mainPanel.add(jPanel12, "card10");

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
        //use case 1
        importFileButton.addActionListener(e -> handleImportFileButtonClick());
        pack();
    }// </editor-fold>

    private void handleImportFileButtonClick() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().endsWith(".csv")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid file type. File must be .csv",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        File selectedFile = file;

        JOptionPane.showMessageDialog(
                this,
                "Selected file:\n" + selectedFile.getName(),
                "File chosen",
                JOptionPane.INFORMATION_MESSAGE
        );
        List<String> data;
        try {
            data = Files.readAllLines(selectedFile.toPath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not read file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (data.size() <= 1) {
            JOptionPane.showMessageDialog(
                    this,
                    "File has no data rows.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        //
        boolean isFormatCorrect = data.stream()
                .skip(1)
                .allMatch(s -> s.matches(
                        "\\d{4}-\\d{2}-\\d{2}\\s*,\\s*[^,]+\\s*,\\s*[^,]+\\s*,-[0-9]+(\\.[0-9]+)?"
                ));

        if (!isFormatCorrect) {
            JOptionPane.showMessageDialog(
                    this,
                    "CSV format is not correct.\n" +
                            "Expected: date, description, merchant, -amount.decimals",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        List<Transaction> transactions = data.stream()
                .skip(1)
                .map(Transaction::of)
                .collect(Collectors.toList()); //imported csv in list format
    }

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        showCard(CARD_HOME);
    }                                          

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        showCard(CARD_IMPORT);
    }                                            

    private void budgetButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        showCard(CARD_BUDGET);
    }                                            

    private void reportButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        showCard(CARD_REPORT);
    }                                            

    private void transactionButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        showCard(CARD_TRANS);
    }                                                 

    private void addBudgetButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                
        showCard(CARD_ADD_BUDGET);
    }                                               

    private void addTransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                     
        showCard(CARD_ADD_TRANS);
    }                                                    

    private void homeButtonMouseEntered(java.awt.event.MouseEvent evt) {                                        
        homeButton.setBackground(new java.awt.Color(150,150,150)); // bluish hover
        homeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }                                       

    private void homeButtonMouseExited(java.awt.event.MouseEvent evt) {                                       
        homeButton.setBackground(new java.awt.Color(100,100,100)); // reset color
        homeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }                                      

    private void transactionButtonMouseEntered(java.awt.event.MouseEvent evt) {                                               
        transactionButton.setBackground(new java.awt.Color(150,150,150)); // bluish hover
        transactionButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }                                              

    private void transactionButtonMouseExited(java.awt.event.MouseEvent evt) {                                              
        transactionButton.setBackground(new java.awt.Color(100,100,100)); // reset color
        transactionButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }                                             

    private void budgetButtonMouseEntered(java.awt.event.MouseEvent evt) {                                          
        budgetButton.setBackground(new java.awt.Color(150,150,150)); // bluish hover
        budgetButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }                                         

    private void budgetButtonMouseExited(java.awt.event.MouseEvent evt) {                                         
        budgetButton.setBackground(new java.awt.Color(100,100,100)); // reset color
        budgetButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }                                        

    private void budgetButtonMousePressed(java.awt.event.MouseEvent evt) {                                          
        budgetButton.setBackground(new java.awt.Color(220, 220, 220)); // lighter gray
    }                                         

    private void budgetButtonMouseClicked(java.awt.event.MouseEvent evt) {                                          
       
    }                                         

    private void transactionButtonMouseClicked(java.awt.event.MouseEvent evt) {                                               
        transactionButton.setBackground(new java.awt.Color(220, 220, 220)); // lighter gray
    }                                              

    private void homeButtonMouseClicked(java.awt.event.MouseEvent evt) {                                        
        homeButton.setBackground(new java.awt.Color(220, 220, 220)); // lighter gray
    }                                       

    private void reportButtonMouseClicked(java.awt.event.MouseEvent evt) {                                          
        reportButton.setBackground(new java.awt.Color(220, 220, 220)); // lighter gray
    }                                         

    private void reportButtonMouseEntered(java.awt.event.MouseEvent evt) {                                          
        reportButton.setBackground(new java.awt.Color(150,150,150)); // bluish hover
        reportButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }                                         

    private void reportButtonMouseExited(java.awt.event.MouseEvent evt) {                                         
        reportButton.setBackground(new java.awt.Color(100,100,100)); // reset color
        reportButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }                                        

    private void importButtonMouseClicked(java.awt.event.MouseEvent evt) {                                          
        importButton.setBackground(new java.awt.Color(220, 220, 220)); // lighter gray
    }                                         

    private void importButtonMouseEntered(java.awt.event.MouseEvent evt) {                                          
        importButton.setBackground(new java.awt.Color(150,150,150)); // bluish hover
        importButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }                                         

    private void importButtonMouseExited(java.awt.event.MouseEvent evt) {                                         
        importButton.setBackground(new java.awt.Color(100,100,100)); // reset color
        importButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }                                        

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void cancelAddTransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                           
        showCard(CARD_TRANS);
    }                                                          

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        showCard(CARD_BUDGET);
    }                                        

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new HomePageView().setVisible(true));
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton addBudgetButton;
    private javax.swing.JButton addTransactionButton;
    private javax.swing.JButton budgetButton;
    private javax.swing.JLabel budgetHeader;
    private javax.swing.JPanel budgetHeaderPanel;
    private javax.swing.JScrollPane budgetScrollPane;
    private javax.swing.JTable budgetTable;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelAddTransactionButton;
    private javax.swing.JButton cancelImportButton;
    private javax.swing.JPanel cardAddBudget;
    private javax.swing.JPanel cardAddTransaction;
    private javax.swing.JPanel cardBudget;
    private javax.swing.JPanel cardCharts;
    private javax.swing.JPanel cardHome;
    private javax.swing.JPanel cardImport;
    private javax.swing.JPanel cardReport;
    private javax.swing.JPanel cardTransaction;
    private javax.swing.JComboBox<String> filterCategoryBox;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JButton homeButton;
    private javax.swing.JPanel importBodyPanel;
    private javax.swing.JButton importButton;
    private javax.swing.JButton importFileButton;
    private javax.swing.JPanel importHeaderPanel;
    private javax.swing.JLabel importLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JButton reportButton;
    private javax.swing.JPanel reportHeaderPanel;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JButton transactionButton;
    private javax.swing.JPanel transactionFilterPanel;
    private javax.swing.JPanel transactionHeader;
    private javax.swing.JPanel transactionHeaderButton;
    private javax.swing.JPanel transactionHeaderHeader;
    private javax.swing.JPanel transactionHeaderTitle;
    private javax.swing.JLabel transactionLabel;
    private javax.swing.JScrollPane transactionScrollPane;
    private javax.swing.JTable transactionTable;
    // End of variables declaration                   
    /** Dotted/dashed rounded rectangle border (upload drop-zone style). */
    static final class DashedRoundBorder implements javax.swing.border.Border {
        private final java.awt.Color color;
        private final float thickness;
        private final float arc;     // corner radius
        private final float dash;    // dash length
        private final float gap;     // gap length
        private final int   pad;     // inner padding

        DashedRoundBorder(java.awt.Color color, float thickness, float arc,
                          float dash, float gap, int pad) {
            this.color = color;
            this.thickness = thickness;
            this.arc = arc;
            this.dash = dash;
            this.gap = gap;
            this.pad = pad;
        }

        @Override
        public void paintBorder(java.awt.Component c, java.awt.Graphics g,
                                int x, int y, int w, int h) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            try {
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new java.awt.BasicStroke(
                        thickness,
                        java.awt.BasicStroke.CAP_ROUND,
                        java.awt.BasicStroke.JOIN_ROUND,
                        10f,
                        new float[]{dash, gap},
                        0f));

                float inset = thickness / 2f;
                float rx = x + pad + inset;
                float ry = y + pad + inset;
                float rw = w - 2f * (pad + inset);
                float rh = h - 2f * (pad + inset);

                g2.draw(new java.awt.geom.RoundRectangle2D.Float(rx, ry, rw, rh, arc, arc));
            } finally {
                g2.dispose();
            }
        }

        @Override
        public java.awt.Insets getBorderInsets(java.awt.Component c) {
            int p = pad + Math.round(thickness);
            return new java.awt.Insets(p, p, p, p);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}

    

