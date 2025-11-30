package frontend;

import api.news.NewsApiGateway;
import api.news.NewsApiGatewayImpl;
import api.news.NewsApiResponse;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.Border;
import org.json.JSONArray;
import org.json.JSONObject;

/*public class NewsApiClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String apiKey;

    public NewsApiClient() {
        this.apiKey = EnvConfig.getNewsApiKey();
    }

    public List<String> fetchTopHeadlines() throws IOException, InterruptedException {
        String url = "https://newsapi.org/v2/top-headlines"
                   + "?country=ca"
                   + "&category=business"
                   + "&pageSize=5"
                   + "&apiKey=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("News API error: " + response.statusCode() + " body=" + response.body());
        }

        JSONObject json = new JSONObject(response.body());
        JSONArray articles = json.getJSONArray("articles");

        List<String> headlines = new ArrayList<>();
        for (int i = 0; i < articles.length(); i++) {
            JSONObject article = articles.getJSONObject(i);
            String title = article.optString("title", "(no title)");
            headlines.add("• " + title);
        }

        return headlines;
    }
}*/

/**
 *
 * @author benja
 */
public class HomePageView extends javax.swing.JFrame {

    // --- Card identifiers ---
    private static final String CARD_HOME     = "newsPanel";
    private static final String CARD_TRANS    = "cardTransaction";
    private static final String CARD_BUDGET   = "cardBudget";
    private static final String CARD_REPORT   = "cardReport";
    private static final String CARD_IMPORT   = "cardImport";
    private static final String CARD_ADD_TRANS   = "cardAddTransaction";
    private static final String CARD_ADD_BUDGET = "cardAddBudget";
    private static final String CARD_EDIT_TRANS = "cardEditTransaction";
    private static final String CARD_EDIT_BUDGET = "cardEditBudget";
    private static final String CARD_EDIT_CATEGORY = "cardEditCategory";
    private static final String CARD_CHOOSE_BUDGET = "cardChooseBudget";
    private static final String CARD_CHOOSE_TRANS = "cardChooseTransaction";
    private int editingRowIndex = -1;
    private javax.swing.JButton deleteTransactionButton;

    private DefaultListModel<String> newsListModel = new DefaultListModel<>();
    private JList<String> newsList = new JList<>(newsListModel);
    private java.util.List<NewsApiResponse.Article> currentArticles = new java.util.ArrayList<>();

    // --- Helper method to switch cards ---
    private void showCard(String cardName) {
        java.awt.CardLayout layout = (java.awt.CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, cardName);
    }

    /**
     * Loads the transaction data from the selected row into the Edit Transaction fields
     * and switches the view.
     * @param row The index of the selected row in the transactionTable.
     */
    private void loadTransactionForEditing(int row) {
        // We need this variable to know which row to update later when the user clicks 'Save Changes'
        editingRowIndex = row;

        // 1. Get the data model from the JTable
        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) transactionTable.getModel();

        // 2. Extract values based on column index (0=Date, 1=Merchant, 2=Description, 3=Amount, 4=Category)
        String dateString = model.getValueAt(row, 0).toString();
        String store = model.getValueAt(row, 1).toString();
        String description = model.getValueAt(row, 2).toString();
        // Convert the Double amount back to a string for the JTextField
        String amount = String.valueOf(model.getValueAt(row, 3));
        String category = model.getValueAt(row, 4).toString();

        // 3. Split the date string back into components (Assuming format "YYYY-MONTH-DD")
        try {
            String[] dateParts = dateString.split("-");
            String year = dateParts[0];
            String month = dateParts[1];
            String day = dateParts[2];

            // Set date ComboBoxes
            editTransactionYearSelect.setSelectedItem(year);
            editTransactionMonthSelect.setSelectedItem(month);
            editTransactionDaySelect.setSelectedItem(day);

        } catch (Exception e) {
            // Handle cases where the date format might be wrong
            System.err.println("Error parsing date for editing: " + e.getMessage());
        }

        // 4. Set the values into the Text Fields and Category ComboBox
        editTransactionStoreEntry.setText(store);
        editTransactionItemEntry.setText(description);
        editTransactionAmountEntry.setText(amount);
        editTransactionCategorySelect.setSelectedItem(category);

        // 5. Finally, switch the view to the edit screen
        // This is the line that performs the screen switch.
        showCard(CARD_EDIT_TRANS);
    }

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(HomePageView.class.getName());

    /**
     * Creates new form HomePage
     */
    public HomePageView() {
        initComponents();
        setupNewsPanel();
        loadNewsAsync();
        // Register each card with its name
        mainPanel.add(newsPanel,        CARD_HOME);
        mainPanel.add(cardTransaction, CARD_TRANS);
        mainPanel.add(cardBudget,      CARD_BUDGET);
        mainPanel.add(cardReport,      CARD_REPORT);
        mainPanel.add(cardImport,      CARD_IMPORT);
        mainPanel.add(cardEditTransaction,CARD_EDIT_TRANS);
        mainPanel.add(cardAddBudget, CARD_ADD_BUDGET);
        mainPanel.add(cardAddTransaction, CARD_ADD_TRANS);
        mainPanel.add(cardEditBudget, CARD_EDIT_BUDGET);
        mainPanel.add(cardEditCategory, CARD_EDIT_CATEGORY);
        mainPanel.add(cardChooseTransaction, CARD_CHOOSE_TRANS);
        mainPanel.add(cardChooseBudget, CARD_CHOOSE_BUDGET);

        // Optional: show a default screen when program starts
        showCard(CARD_HOME);

    }

    private void setupNewsPanel() {
       newsPanel.setLayout(new BorderLayout());
        newsPanel.setBackground(new Color(245, 245, 245));

        // style list
        newsList.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        newsList.setBackground(new Color(245, 245, 245));
        newsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newsList.setFocusable(false);

        // prettier renderer with wrapping (optional but nice)
        newsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                String text = (value == null) ? "" : value.toString();
                label.setText("<html><body style='width:520px; padding:3px 0;'>" +
                            text + "</body></html>");

                label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                if (isSelected) {
                    label.setBackground(new Color(220, 235, 255));
                } else {
                    label.setBackground(new Color(245, 245, 245));
                }
                return label;
            }
        });

        // 🔹 double-click to open link
        newsList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = newsList.locationToIndex(e.getPoint());
                    if (index >= 0 && index < currentArticles.size()) {
                        NewsApiResponse.Article article = currentArticles.get(index);
                        if (article != null && article.url != null && !article.url.isBlank()) {
                            try {
                                if (java.awt.Desktop.isDesktopSupported()) {
                                    java.awt.Desktop.getDesktop().browse(new java.net.URI(article.url));
                                } else {
                                    JOptionPane.showMessageDialog(
                                            HomePageView.this,
                                            "Opening links is not supported on this platform.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                        HomePageView.this,
                                        "Could not open link: " + ex.getMessage(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(newsList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        newsPanel.add(scrollPane, BorderLayout.CENTER);

        newsListModel.clear();
        newsListModel.addElement("Loading latest financial headlines…");
    }

    private void loadNewsAsync() {
        javax.swing.SwingWorker<java.util.List<String>, Void> worker =
                new javax.swing.SwingWorker<>() {
            @Override
            protected java.util.List<String> doInBackground() throws Exception {
                NewsApiGateway gateway = new NewsApiGatewayImpl();

                NewsApiGateway.TopHeadlinesRequest req = new NewsApiGateway.TopHeadlinesRequest();
                req.country = "us";
                req.category = "business";

                NewsApiResponse resp = gateway.getTopHeadlines(req);

                java.util.List<String> titles = new java.util.ArrayList<>();
                if (resp.articles != null) {
                    for (NewsApiResponse.Article a : resp.articles) {
                        if (a.title != null) {
                            titles.add("• " + a.title);
                        }
                    }
                }
                return titles;
            }

            @Override
            protected void done() {
                try {
                    java.util.List<String> titles = get();
                    newsListModel.clear();
                    for (String t : titles) {
                        newsListModel.addElement(t);
                    }
                } catch (Exception e) {
                    newsListModel.clear();
                    newsListModel.addElement("Failed to load news: " + e.getMessage());
                }
            }
        };

        worker.execute();
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
        newsPanel = new javax.swing.JPanel();
        cardTransaction = new javax.swing.JPanel();
        transactionHeader = new javax.swing.JPanel();
        transactionHeaderHeader = new javax.swing.JPanel();
        transactionHeaderButton = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        addTransactionButton = new javax.swing.JButton();
        editTransactionButton = new javax.swing.JButton();
        transactionFilterPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        transactionFilterCategorySelect = new javax.swing.JComboBox<>();
        editCategoryButton = new javax.swing.JButton();
        transactionScrollPane = new javax.swing.JScrollPane();
        transactionTable = new javax.swing.JTable();
        cardBudget = new javax.swing.JPanel();
        budgetHeaderPanel = new javax.swing.JPanel();
        budgetHeader = new javax.swing.JLabel();
        addBudgetButton = new javax.swing.JButton();
        editBudgetButton = new javax.swing.JButton();
        budgetScrollPane = new javax.swing.JScrollPane();
        budgetTable = new javax.swing.JTable();
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
        reportCategorySelected = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        generateReportButton = new javax.swing.JButton();
        reportBodyPanel = new javax.swing.JPanel();
        cardAddBudget = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        addBudgetAmountEntry = new javax.swing.JTextField();
        addBudgetCancelButton = new javax.swing.JButton();
        addBudgetFinishButton = new javax.swing.JButton();
        addBudgetCategorySelect = new javax.swing.JComboBox<>();
        cardImport = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        importFileButton = new javax.swing.JButton();
        cardAddTransaction = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        addTransactionYearSelect = new javax.swing.JComboBox<>();
        addTransactionMonthSelect = new javax.swing.JComboBox<>();
        addTransactionDaySelect = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        addTransactionAmountEntry = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        addTransactionStoreEntry = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        addTransactionCategorySelect = new javax.swing.JComboBox<>();
        cancelAdtTransactionButton = new javax.swing.JButton();
        finishAddTransactionButton = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        addTransactionItemEntry = new javax.swing.JTextField();
        cardEditCategory = new javax.swing.JPanel();
        cardChooseTransaction = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        chooseTransactionChooseButton = new javax.swing.JButton();
        chooseTransactionCancelButton = new javax.swing.JButton();
        chooseTransactionSelectScrollPane = new javax.swing.JScrollPane();
        deleteTransactionButton = new javax.swing.JButton();
        cardChooseBudget = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        chooseBudgetChooseButton = new javax.swing.JButton();
        chooseBudgetCancelButton = new javax.swing.JButton();
        chooseBudgetSelectScrollPane = new javax.swing.JScrollPane();
        cardEditTransaction = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        editTransactionYearSelect = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        editTransactionMonthSelect = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        editTransactionDaySelect = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        editTransactionAmountEntry = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        editTransactionStoreEntry = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        editTransactionItemEntry = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        editTransactionCategorySelect = new javax.swing.JComboBox<>();
        editTransactionCancelButton = new javax.swing.JButton();
        editTransactionEditButton = new javax.swing.JButton();
        cardEditBudget = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        editBudgetAmountEntry = new javax.swing.JTextField();
        label = new javax.swing.JLabel();
        editBudgetCategorySelect = new javax.swing.JComboBox<>();
        editBudgetEditButton = new javax.swing.JButton();
        editBudgetCancelButton = new javax.swing.JButton();

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

        javax.swing.GroupLayout cardHomeLayout = new javax.swing.GroupLayout(newsPanel);
        newsPanel.setLayout(cardHomeLayout);
        cardHomeLayout.setHorizontalGroup(
                cardHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 551, Short.MAX_VALUE)
        );
        cardHomeLayout.setVerticalGroup(
                cardHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 604, Short.MAX_VALUE)
        );

        mainPanel.add(newsPanel, "card2");

        cardTransaction.setLayout(new java.awt.BorderLayout());

        transactionHeader.setPreferredSize(new java.awt.Dimension(445, 80));
        transactionHeader.setLayout(new java.awt.BorderLayout());

        transactionHeaderHeader.setPreferredSize(new java.awt.Dimension(445, 50));
        transactionHeaderHeader.setLayout(new java.awt.BorderLayout());

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel32.setText("Transactions");

        addTransactionButton.setText("Add Transaction [+]");
        addTransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTransactionButtonActionPerformed(evt);
            }
        });

        editTransactionButton.setText("Edit Transaction [-]");
        editTransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTransactionButtonActionPerformed(evt);
            }
        });

        deleteTransactionButton.setBackground(new java.awt.Color(255, 153, 51)); // Orange/Warning color
        deleteTransactionButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        deleteTransactionButton.setText("Delete Transaction");
        deleteTransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTransactionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout transactionHeaderButtonLayout = new javax.swing.GroupLayout(transactionHeaderButton);
        transactionHeaderButton.setLayout(transactionHeaderButtonLayout);
        transactionHeaderButtonLayout.setHorizontalGroup(
                transactionHeaderButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(transactionHeaderButtonLayout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(jLabel32)
                                .addGap(18, 18, 18)
                                .addComponent(addTransactionButton)
                                .addGap(18, 18, 18)
                                .addComponent(editTransactionButton)
                                .addGap(18, 18, 18)
                                .addComponent(deleteTransactionButton)
                                .addContainerGap(58, Short.MAX_VALUE))
        );
        transactionHeaderButtonLayout.setVerticalGroup(
                transactionHeaderButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(transactionHeaderButtonLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(transactionHeaderButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(editTransactionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(addTransactionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(deleteTransactionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(12, Short.MAX_VALUE))
        );

        transactionHeaderHeader.add(transactionHeaderButton, java.awt.BorderLayout.CENTER);

        transactionHeader.add(transactionHeaderHeader, java.awt.BorderLayout.PAGE_START);

        transactionFilterPanel.setMinimumSize(new java.awt.Dimension(100, 30));
        transactionFilterPanel.setPreferredSize(new java.awt.Dimension(445, 50));

        filterLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        filterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        filterLabel.setText("Filter by Category:");

        transactionFilterCategorySelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Category...", "Dining", "Leisure", "Gifts", "School" }));

        editCategoryButton.setText("Edit Category [+]");
        editCategoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCategoryButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout transactionFilterPanelLayout = new javax.swing.GroupLayout(transactionFilterPanel);
        transactionFilterPanel.setLayout(transactionFilterPanelLayout);
        transactionFilterPanelLayout.setHorizontalGroup(
                transactionFilterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(transactionFilterPanelLayout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(filterLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(transactionFilterCategorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(65, 65, 65)
                                .addComponent(editCategoryButton)
                                .addContainerGap(79, Short.MAX_VALUE))
        );
        transactionFilterPanelLayout.setVerticalGroup(
                transactionFilterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(transactionFilterPanelLayout.createSequentialGroup()
                                .addGroup(transactionFilterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(filterLabel)
                                        .addComponent(transactionFilterCategorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(editCategoryButton))
                                .addGap(0, 7, Short.MAX_VALUE))
        );

        transactionHeader.add(transactionFilterPanel, java.awt.BorderLayout.CENTER);

        cardTransaction.add(transactionHeader, java.awt.BorderLayout.PAGE_START);

        transactionScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        transactionTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "Date", "Merchant", "Description", "Amount", "Category"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Double.class, java.lang.String.class
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

        editBudgetButton.setText("Edit Budget [-]");
        editBudgetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBudgetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout budgetHeaderPanelLayout = new javax.swing.GroupLayout(budgetHeaderPanel);
        budgetHeaderPanel.setLayout(budgetHeaderPanelLayout);
        budgetHeaderPanelLayout.setHorizontalGroup(
                budgetHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(budgetHeaderPanelLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(budgetHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addBudgetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(editBudgetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(30, Short.MAX_VALUE))
        );
        budgetHeaderPanelLayout.setVerticalGroup(
                budgetHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, budgetHeaderPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(budgetHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(budgetHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                                        .addComponent(addBudgetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(editBudgetButton))
                                .addContainerGap())
        );

        cardBudget.add(budgetHeaderPanel, java.awt.BorderLayout.PAGE_START);

        budgetScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        budgetTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "Category", "Budget", "Spent", "Remaining"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false
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
        }

        cardBudget.add(budgetScrollPane, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardBudget, "card4");

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

        reportCategorySelected.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Dining", "Leisure", "Work", "School" }));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addContainerGap(76, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(reportCategorySelected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50))
        );
        jPanel8Layout.setVerticalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(reportCategorySelected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel8, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel6.setPreferredSize(new java.awt.Dimension(462, 62));

        generateReportButton.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        generateReportButton.setText("Generate Report");
        generateReportButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addContainerGap(172, Short.MAX_VALUE)
                                .addComponent(generateReportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(167, 167, 167))
        );
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addContainerGap(12, Short.MAX_VALUE)
                                .addComponent(generateReportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        jPanel4.add(jPanel6, java.awt.BorderLayout.CENTER);

        reportHeaderPanel.add(jPanel4, java.awt.BorderLayout.CENTER);

        cardReport.add(reportHeaderPanel, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout reportBodyPanelLayout = new javax.swing.GroupLayout(reportBodyPanel);
        reportBodyPanel.setLayout(reportBodyPanelLayout);
        reportBodyPanelLayout.setHorizontalGroup(
                reportBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 551, Short.MAX_VALUE)
        );
        reportBodyPanelLayout.setVerticalGroup(
                reportBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 454, Short.MAX_VALUE)
        );

        cardReport.add(reportBodyPanel, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardReport, "card6");

        cardAddBudget.setLayout(new java.awt.BorderLayout());

        jPanel10.setBackground(new java.awt.Color(200, 200, 200));
        jPanel10.setPreferredSize(new java.awt.Dimension(551, 50));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel14.setText("Set Budget Limit");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
                jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addContainerGap(186, Short.MAX_VALUE)
                                .addComponent(jLabel14)
                                .addGap(176, 176, 176))
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

        addBudgetAmountEntry.setText("0.00");
        addBudgetAmountEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBudgetAmountEntryActionPerformed(evt);
            }
        });

        addBudgetCancelButton.setBackground(new java.awt.Color(255, 0, 0));
        addBudgetCancelButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addBudgetCancelButton.setText("Cancel");
        addBudgetCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBudgetCancelButtonActionPerformed(evt);
            }
        });

        addBudgetFinishButton.setBackground(new java.awt.Color(0, 255, 0));
        addBudgetFinishButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addBudgetFinishButton.setText("Set Limit");
        addBudgetFinishButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBudgetFinishButtonActionPerformed(evt);
            }
        });

        addBudgetCategorySelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
                jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel13Layout.createSequentialGroup()
                                                .addComponent(jLabel16)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(addBudgetCategorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(addBudgetCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(73, 73, 73)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel13Layout.createSequentialGroup()
                                                .addComponent(jLabel17)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(addBudgetAmountEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(addBudgetFinishButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(87, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
                jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel17)
                                        .addComponent(addBudgetAmountEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel16)
                                        .addComponent(addBudgetCategorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(38, 38, 38)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(addBudgetCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(addBudgetFinishButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(414, Short.MAX_VALUE))
        );

        cardAddBudget.add(jPanel13, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardAddBudget, "card9");

        cardImport.setLayout(new java.awt.BorderLayout());

        jPanel17.setBackground(new java.awt.Color(200, 200, 200));
        jPanel17.setPreferredSize(new java.awt.Dimension(551, 50));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel33.setText("Import File");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
                jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel17Layout.createSequentialGroup()
                                .addGap(203, 203, 203)
                                .addComponent(jLabel33)
                                .addContainerGap(223, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
                jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel17Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                                .addContainerGap())
        );

        cardImport.add(jPanel17, java.awt.BorderLayout.PAGE_START);

        importFileButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        importFileButton.setText("Import");
        importFileButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
                jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel18Layout.createSequentialGroup()
                                .addGap(224, 224, 224)
                                .addComponent(importFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(244, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
                jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel18Layout.createSequentialGroup()
                                .addGap(193, 193, 193)
                                .addComponent(importFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(320, Short.MAX_VALUE))
        );

        cardImport.add(jPanel18, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardImport, "card11");

        cardAddTransaction.setLayout(new java.awt.BorderLayout());

        jPanel11.setBackground(new java.awt.Color(200, 200, 200));
        jPanel11.setPreferredSize(new java.awt.Dimension(551, 50));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel19.setText("Add Transaction");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
                jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addContainerGap(161, Short.MAX_VALUE)
                                .addComponent(jLabel19)
                                .addGap(205, 205, 205))
        );
        jPanel11Layout.setVerticalGroup(
                jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                                .addContainerGap())
        );

        cardAddTransaction.add(jPanel11, java.awt.BorderLayout.PAGE_START);

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setText("Date:");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setText("Year:");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setText("Month:");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel23.setText("Day:");

        addTransactionYearSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2025", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016" }));

        addTransactionMonthSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        addTransactionDaySelect.setMaximumRowCount(32);
        addTransactionDaySelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setText("Amount:");

        addTransactionAmountEntry.setText("0.00");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel26.setText("Store Name:");

        addTransactionStoreEntry.setText("Store");
        addTransactionStoreEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTransactionStoreEntryActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel27.setText("Category:");

        addTransactionCategorySelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select...", "Dining", "Leisure", "Gifts", "Work" }));

        cancelAdtTransactionButton.setBackground(new java.awt.Color(255, 0, 0));
        cancelAdtTransactionButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelAdtTransactionButton.setText("Cancel");
        cancelAdtTransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAdtTransactionButtonActionPerformed(evt);
            }
        });

        finishAddTransactionButton.setBackground(new java.awt.Color(0, 255, 0));
        finishAddTransactionButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        finishAddTransactionButton.setText("Add");
        finishAddTransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishAddTransactionButtonActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel28.setText("Item Name:");

        addTransactionItemEntry.setText("Item");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
                jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                                .addGap(116, 116, 116)
                                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                                .addGap(38, 38, 38)
                                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                                                                .addComponent(jLabel26)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(addTransactionStoreEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(cancelAdtTransactionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                                                                .addGap(70, 70, 70)
                                                                                .addComponent(jLabel28)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(addTransactionItemEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                                                                .addGap(115, 115, 115)
                                                                                .addComponent(finishAddTransactionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                                                .addComponent(jLabel24)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(addTransactionAmountEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel21)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(addTransactionYearSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel22)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(addTransactionMonthSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(12, 12, 12)
                                                                .addComponent(jLabel23)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(addTransactionDaySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                                                .addComponent(jLabel27)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(addTransactionCategorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addContainerGap(82, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
                jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel20)
                                        .addComponent(jLabel21)
                                        .addComponent(addTransactionYearSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel22)
                                        .addComponent(addTransactionMonthSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel23)
                                        .addComponent(addTransactionDaySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel24)
                                        .addComponent(addTransactionAmountEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(addTransactionStoreEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel28)
                                        .addComponent(jLabel26)
                                        .addComponent(addTransactionItemEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel27)
                                        .addComponent(addTransactionCategorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cancelAdtTransactionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(finishAddTransactionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(361, Short.MAX_VALUE))
        );

        cardAddTransaction.add(jPanel12, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardAddTransaction, "card8");

        javax.swing.GroupLayout cardEditCategoryLayout = new javax.swing.GroupLayout(cardEditCategory);
        cardEditCategory.setLayout(cardEditCategoryLayout);
        cardEditCategoryLayout.setHorizontalGroup(
                cardEditCategoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 551, Short.MAX_VALUE)
        );
        cardEditCategoryLayout.setVerticalGroup(
                cardEditCategoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 604, Short.MAX_VALUE)
        );

        mainPanel.add(cardEditCategory, "card13");

        cardChooseTransaction.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(200, 200, 200));
        jPanel1.setPreferredSize(new java.awt.Dimension(551, 50));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Choose Transaction");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(171, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addGap(161, 161, 161))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        cardChooseTransaction.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jPanel14.setPreferredSize(new java.awt.Dimension(551, 350));

        chooseTransactionChooseButton.setBackground(new java.awt.Color(0, 255, 0));
        chooseTransactionChooseButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        chooseTransactionChooseButton.setText("Choose");
        chooseTransactionChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseTransactionChooseButtonActionPerformed(evt);
            }
        });

        chooseTransactionCancelButton.setBackground(new java.awt.Color(255, 0, 0));
        chooseTransactionCancelButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        chooseTransactionCancelButton.setText("Cancel");
        chooseTransactionCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseTransactionCancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
                jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                .addGap(105, 105, 105)
                                .addComponent(chooseTransactionCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 144, Short.MAX_VALUE)
                                .addComponent(chooseTransactionChooseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(89, 89, 89))
        );
        jPanel14Layout.setVerticalGroup(
                jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(chooseTransactionCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(chooseTransactionChooseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(307, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel14, java.awt.BorderLayout.PAGE_END);

        chooseTransactionSelectScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        chooseTransactionSelectScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chooseTransactionSelectScrollPane.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(chooseTransactionSelectScrollPane, java.awt.BorderLayout.CENTER);

        cardChooseTransaction.add(jPanel9, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardChooseTransaction, "card14");

        cardChooseBudget.setLayout(new java.awt.BorderLayout());

        jPanel15.setBackground(new java.awt.Color(200, 200, 200));
        jPanel15.setPreferredSize(new java.awt.Dimension(551, 50));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setText("Choose Budget");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
                jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                                .addContainerGap(198, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addGap(183, 183, 183))
        );
        jPanel15Layout.setVerticalGroup(
                jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                                .addContainerGap())
        );

        cardChooseBudget.add(jPanel15, java.awt.BorderLayout.PAGE_START);

        jPanel16.setLayout(new java.awt.BorderLayout());

        jPanel19.setPreferredSize(new java.awt.Dimension(551, 350));

        chooseBudgetChooseButton.setBackground(new java.awt.Color(0, 255, 0));
        chooseBudgetChooseButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        chooseBudgetChooseButton.setText("Choose");
        chooseBudgetChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseBudgetChooseButtonActionPerformed(evt);
            }
        });

        chooseBudgetCancelButton.setBackground(new java.awt.Color(255, 0, 0));
        chooseBudgetCancelButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        chooseBudgetCancelButton.setText("Cancel");
        chooseBudgetCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseBudgetCancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
                jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                                .addGap(117, 117, 117)
                                .addComponent(chooseBudgetCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 134, Short.MAX_VALUE)
                                .addComponent(chooseBudgetChooseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(84, 84, 84))
        );
        jPanel19Layout.setVerticalGroup(
                jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel19Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(chooseBudgetChooseButton, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                                        .addComponent(chooseBudgetCancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(302, Short.MAX_VALUE))
        );

        jPanel16.add(jPanel19, java.awt.BorderLayout.PAGE_END);

        chooseBudgetSelectScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chooseBudgetSelectScrollPane.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel16.add(chooseBudgetSelectScrollPane, java.awt.BorderLayout.CENTER);

        cardChooseBudget.add(jPanel16, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardChooseBudget, "card11");

        cardEditTransaction.setLayout(new java.awt.BorderLayout());

        jPanel22.setBackground(new java.awt.Color(200, 200, 200));
        jPanel22.setPreferredSize(new java.awt.Dimension(551, 50));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel8.setText("Edit Transaction");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
                jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                                .addContainerGap(192, Short.MAX_VALUE)
                                .addComponent(jLabel8)
                                .addGap(177, 177, 177))
        );
        jPanel22Layout.setVerticalGroup(
                jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        cardEditTransaction.add(jPanel22, java.awt.BorderLayout.PAGE_START);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Date:");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Year:");

        editTransactionYearSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2025", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016" }));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Month:");

        editTransactionMonthSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"}));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Day:");

        editTransactionDaySelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"  }));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("Amount:");

        editTransactionAmountEntry.setText("0.00");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel29.setText("Store Name:");

        editTransactionStoreEntry.setText("Store");

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel30.setText("Item Name:");

        editTransactionItemEntry.setText("Item");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel31.setText("Category:");

        editTransactionCategorySelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select...", "Dining", "Leisure", "Gifts", "Work" }));

        editTransactionCancelButton.setBackground(new java.awt.Color(255, 0, 0));
        editTransactionCancelButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        editTransactionCancelButton.setText("Cancel");
        editTransactionCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTransactionCancelButtonActionPerformed(evt);
            }
        });

        editTransactionEditButton.setBackground(new java.awt.Color(51, 255, 0));
        editTransactionEditButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        editTransactionEditButton.setText("Edit");
        editTransactionEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTransactionEditButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
                jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel23Layout.createSequentialGroup()
                                                .addComponent(jLabel18)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(editTransactionAmountEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel23Layout.createSequentialGroup()
                                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(editTransactionYearSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel11)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(editTransactionMonthSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel13)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(editTransactionDaySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel23Layout.createSequentialGroup()
                                                .addComponent(jLabel31)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(editTransactionCategorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel23Layout.createSequentialGroup()
                                                .addComponent(jLabel29)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(editTransactionStoreEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(33, 33, 33)
                                                .addComponent(jLabel30)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(editTransactionItemEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel23Layout.createSequentialGroup()
                                                .addGap(44, 44, 44)
                                                .addComponent(editTransactionCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(135, 135, 135)
                                                .addComponent(editTransactionEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap(67, Short.MAX_VALUE))
        );

        jPanel23Layout.setVerticalGroup(
                jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel9)
                                        .addComponent(jLabel10)
                                        .addComponent(editTransactionYearSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel11)
                                        .addComponent(editTransactionMonthSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel13)
                                        .addComponent(editTransactionDaySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel18)
                                        .addComponent(editTransactionAmountEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel29)
                                        .addComponent(editTransactionStoreEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel30)
                                        .addComponent(editTransactionItemEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel31)
                                        .addComponent(editTransactionCategorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(editTransactionCancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                                        .addComponent(editTransactionEditButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(348, Short.MAX_VALUE))
        );

        cardEditTransaction.add(jPanel23, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardEditTransaction, "card12");

        cardEditBudget.setLayout(new java.awt.BorderLayout());

        jPanel20.setBackground(new java.awt.Color(200, 200, 200));
        jPanel20.setPreferredSize(new java.awt.Dimension(551, 50));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel6.setText("Edit Budget");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
                jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGap(206, 206, 206)
                                .addComponent(jLabel6)
                                .addContainerGap(211, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
                jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 2, Short.MAX_VALUE))
        );

        cardEditBudget.add(jPanel20, java.awt.BorderLayout.PAGE_START);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Amount: ");

        editBudgetAmountEntry.setText("0.00");

        label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        label.setText("Select Category:");

        editBudgetCategorySelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        editBudgetEditButton.setBackground(new java.awt.Color(51, 255, 0));
        editBudgetEditButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        editBudgetEditButton.setText("Edit");

        editBudgetCancelButton.setBackground(new java.awt.Color(255, 0, 51));
        editBudgetCancelButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        editBudgetCancelButton.setText("Cancel");
        editBudgetCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBudgetCancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
                jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel21Layout.createSequentialGroup()
                                .addGap(92, 92, 92)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editBudgetAmountEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58)
                                .addComponent(label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(editBudgetCategorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(73, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                                .addGap(121, 121, 121)
                                .addComponent(editBudgetCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(editBudgetEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(105, 105, 105))
        );
        jPanel21Layout.setVerticalGroup(
                jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel21Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel7)
                                        .addComponent(editBudgetAmountEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label)
                                        .addComponent(editBudgetCategorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(51, 51, 51)
                                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(editBudgetEditButton, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                                        .addComponent(editBudgetCancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(414, Short.MAX_VALUE))
        );

        cardEditBudget.add(jPanel21, java.awt.BorderLayout.CENTER);

        mainPanel.add(cardEditBudget, "card13");

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

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
                        "\\d{4}-\\d{2}-\\d{2}\\s*,\\s*[^,]+\\s*,\\s*[^,]+\\s*,-?[0-9]+(\\.[0-9]+)?"
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

    private void addBudgetAmountEntryActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void addBudgetCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_BUDGET);
    }

    private void addBudgetFinishButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void editBudgetButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_CHOOSE_BUDGET);
    }

    private void addTransactionStoreEntryActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void cancelAdtTransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_TRANS);
    }

    private void editTransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = transactionTable.getSelectedRow();

        if (selectedRow == -1) {
            // No row is selected, show an error message.
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please select a transaction from the list to edit.",
                    "No Selection",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ⭐️ This is the crucial line: call the data loading method.
        loadTransactionForEditing(selectedRow);

        // NOTE: Make sure there is NO showCard(CARD_CHOOSE_TRANS) or other showCard() call
        // after this, as loadTransactionForEditing() handles the switch.
    }

    private void editCategoryButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_EDIT_CATEGORY);
    }

    private void addTransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_ADD_TRANS);
    }

    private void chooseTransactionCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_TRANS);
    }

    private void chooseTransactionChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_EDIT_TRANS);
    }

    private void chooseBudgetCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_BUDGET);
    }

    private void chooseBudgetChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_EDIT_BUDGET);
    }

    private void editBudgetCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_BUDGET);
    }

    private void editTransactionEditButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // 1. Retrieve data from the Edit form fields
        String store = editTransactionStoreEntry.getText().trim();
        String description = editTransactionItemEntry.getText().trim();
        String amountText = editTransactionAmountEntry.getText().trim();
        String category = (String) editTransactionCategorySelect.getSelectedItem();

        // Construct the date string (e.g., "2024-October-15")
        String year = (String) editTransactionYearSelect.getSelectedItem();
        String month = (String) editTransactionMonthSelect.getSelectedItem();
        String day = (String) editTransactionDaySelect.getSelectedItem();
        String dateString = year + "-" + month + "-" + day;

        // 2. Validation (Checking for missing/default values)
        if (store.isEmpty() || amountText.isEmpty() || amountText.equals("0.00")) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please fill in the Store Name and enter a non-zero Amount.",
                    "Missing Information",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check for default/placeholder values in JComboBoxes (Fixes the issue where validation is skipped)
        if (year.contains("Item") || month.contains("Item") || day.contains("Item") ||
                category.contains("Item")) {

            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please select a valid Date and Category.",
                    "Missing Information",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 3. Parse the amount and handle NumberFormatException
            double amount = Double.parseDouble(amountText);
            if (amount < 0) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Amount cannot be negative. Please enter a positive value.",
                        "Invalid Amount",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                return; // Stop execution
            }
            // 4. Update the JTable Model (Using the stored row index)
            javax.swing.table.DefaultTableModel model =
                    (javax.swing.table.DefaultTableModel) transactionTable.getModel();

            // Use the editingRowIndex saved in loadTransactionForEditing()
            // The column indices must match your table: 0=Date, 1=Merchant, 2=Description, 3=Amount, 4=Category
            model.setValueAt(dateString, editingRowIndex, 0);
            model.setValueAt(store, editingRowIndex, 1);
            model.setValueAt(description, editingRowIndex, 2);
            model.setValueAt(amount, editingRowIndex, 3);
            model.setValueAt(category, editingRowIndex, 4);

            // 5. Show Success Message
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Transaction updated successfully!",
                    "Success",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

            // 6. Return to the main Transaction list view
            showCard(CARD_TRANS);

        } catch (NumberFormatException e) {
            // Validation for invalid number input
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for the amount (e.g., 10.50).",
                    "Invalid Input",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editTransactionCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        showCard(CARD_TRANS);
    }

    private void finishAddTransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // 1. Retrieve data
        String store = addTransactionStoreEntry.getText().trim();
        String description = addTransactionItemEntry.getText().trim();
        String amountText = addTransactionAmountEntry.getText().trim();
        String category = (String) addTransactionCategorySelect.getSelectedItem();

        // Date components
        String year = (String) addTransactionYearSelect.getSelectedItem();
        String month = (String) addTransactionMonthSelect.getSelectedItem();
        String day = (String) addTransactionDaySelect.getSelectedItem();
        String dateString = year + "-" + month + "-" + day;

        // A. VALIDATION FOR REQUIRED TEXT FIELDS (Store and Amount)
        if (store.isEmpty() || store.equalsIgnoreCase("Store") || amountText.isEmpty() || amountText.equals("0.00")) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please fill in the Store Name and enter a non-zero Amount.",
                    "Missing Information",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return; // Stop here if text fields are missing/default
        }

        // B. VALIDATION FOR REQUIRED COMBOBOXES (Date and Category)
        if (year.equals("Select") || month.equals("Select") || day.equals("Select") ||
                category.contains("Select")) {

            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please select a valid Date (Year, Month, Day) and Category.",
                    "Missing Information",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return; // Stop here if dropdowns are missing/default
        }

        // C. VALIDATION FOR DESCRIPTION (Optional, but useful to check default)
        if (description.equalsIgnoreCase("Item")) {
            description = "";
        }


        try {
            // 3. Parse the amount to ensure it is a valid number
            double amount = Double.parseDouble(amountText);
            if (amount < 0) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Amount cannot be negative. Please enter a positive value.",
                        "Invalid Amount",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                return; // Stop execution
            }

            // 4. Update the JTable (Visual update)
            javax.swing.table.DefaultTableModel model =
                    (javax.swing.table.DefaultTableModel) transactionTable.getModel();

            // Order matches your table columns: [Date, Merchant, Description, Amount, Category]
            model.addRow(new Object[]{dateString, store, description, amount, category});

            // 5. Success Message and Cleanup
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Transaction added successfully!",
                    "Success",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

            addTransactionStoreEntry.setText("");
            addTransactionItemEntry.setText("");
            addTransactionAmountEntry.setText("");

            // 6. Return to the main Transaction list view
            showCard(CARD_TRANS);

        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for the amount (e.g., 10.50).",
                    "Invalid Input",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Handles the deletion of a selected transaction row.
     */
    private void deleteTransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // 1. Get the index of the selected row
        int selectedRow = transactionTable.getSelectedRow();

        // 2. Validation: Check if a row is actually selected
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Please select a transaction to delete.",
                    "No Selection",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Confirmation Dialog (CRITICAL STEP)
        int confirmResult = javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete the selected transaction?",
                "Confirm Deletion",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);

        // Check if the user clicked "Yes"
        if (confirmResult == javax.swing.JOptionPane.YES_OPTION) {

            // 4. Delete the row from the JTable model
            javax.swing.table.DefaultTableModel model =
                    (javax.swing.table.DefaultTableModel) transactionTable.getModel();

            // Use the index to remove the row
            model.removeRow(selectedRow);

            // 5. Show Success Message
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Transaction successfully deleted.",
                    "Deleted",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
        // If the user selects "No," the method simply returns, and no action is taken.
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
    private javax.swing.JTextField addBudgetAmountEntry;
    private javax.swing.JButton addBudgetButton;
    private javax.swing.JButton addBudgetCancelButton;
    private javax.swing.JComboBox<String> addBudgetCategorySelect;
    private javax.swing.JButton addBudgetFinishButton;
    private javax.swing.JTextField addTransactionAmountEntry;
    private javax.swing.JButton addTransactionButton;
    private javax.swing.JComboBox<String> addTransactionCategorySelect;
    private javax.swing.JComboBox<String> addTransactionDaySelect;
    private javax.swing.JTextField addTransactionItemEntry;
    private javax.swing.JComboBox<String> addTransactionMonthSelect;
    private javax.swing.JTextField addTransactionStoreEntry;
    private javax.swing.JComboBox<String> addTransactionYearSelect;
    private javax.swing.JButton budgetButton;
    private javax.swing.JLabel budgetHeader;
    private javax.swing.JPanel budgetHeaderPanel;
    private javax.swing.JScrollPane budgetScrollPane;
    private javax.swing.JTable budgetTable;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelAdtTransactionButton;
    private javax.swing.JPanel cardAddBudget;
    private javax.swing.JPanel cardAddTransaction;
    private javax.swing.JPanel cardBudget;
    private javax.swing.JPanel cardChooseBudget;
    private javax.swing.JPanel cardChooseTransaction;
    private javax.swing.JPanel cardEditBudget;
    private javax.swing.JPanel cardEditCategory;
    private javax.swing.JPanel cardEditTransaction;
    private javax.swing.JPanel cardImport;
    private javax.swing.JPanel cardReport;
    private javax.swing.JPanel cardTransaction;
    private javax.swing.JButton chooseBudgetCancelButton;
    private javax.swing.JButton chooseBudgetChooseButton;
    private javax.swing.JScrollPane chooseBudgetSelectScrollPane;
    private javax.swing.JButton chooseTransactionCancelButton;
    private javax.swing.JButton chooseTransactionChooseButton;
    private javax.swing.JScrollPane chooseTransactionSelectScrollPane;
    private javax.swing.JTextField editBudgetAmountEntry;
    private javax.swing.JButton editBudgetButton;
    private javax.swing.JButton editBudgetCancelButton;
    private javax.swing.JComboBox<String> editBudgetCategorySelect;
    private javax.swing.JButton editBudgetEditButton;
    private javax.swing.JButton editCategoryButton;
    private javax.swing.JTextField editTransactionAmountEntry;
    private javax.swing.JButton editTransactionButton;
    private javax.swing.JButton editTransactionCancelButton;
    private javax.swing.JComboBox<String> editTransactionCategorySelect;
    private javax.swing.JComboBox<String> editTransactionDaySelect;
    private javax.swing.JButton editTransactionEditButton;
    private javax.swing.JTextField editTransactionItemEntry;
    private javax.swing.JComboBox<String> editTransactionMonthSelect;
    private javax.swing.JTextField editTransactionStoreEntry;
    private javax.swing.JComboBox<String> editTransactionYearSelect;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JButton finishAddTransactionButton;
    private javax.swing.JButton generateReportButton;
    private javax.swing.JButton homeButton;
    private javax.swing.JButton importButton;
    private javax.swing.JButton importFileButton;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
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
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel label;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel newsPanel;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JPanel reportBodyPanel;
    private javax.swing.JButton reportButton;
    private javax.swing.JComboBox<String> reportCategorySelected;
    private javax.swing.JPanel reportHeaderPanel;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JButton transactionButton;
    private javax.swing.JComboBox<String> transactionFilterCategorySelect;
    private javax.swing.JPanel transactionFilterPanel;
    private javax.swing.JPanel transactionHeader;
    private javax.swing.JPanel transactionHeaderButton;
    private javax.swing.JPanel transactionHeaderHeader;
    private javax.swing.JScrollPane transactionScrollPane;
    private javax.swing.JTable transactionTable;
    // End of variables declaration                   
}