//PACKAGE
package YGDB;

//IMPORTS
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.util.ArrayList;
import java.util.List;

//CLASS
public class YGDB extends JFrame {
    BufferedImage art = null;
    Image scaledArt = null;
    ImageIcon finalArt = null;
    //Constants
    // private static final String urlBase = "https://db.ygoprodeck.com/api/v7"; //Base of the YGOProdeck API
    // private static final String api = urlBase + "/cardinfo.php"; //The card info... the actual API
    // private static final String dbVer = urlBase + "/checkDBVer.php"; //The database version
    // private static final String currResLoc = "data/currResult.json"; //Location of the current result
    // private static final String fullDB = "data/fullDB.json"; //Location of the full database. Used when updating
    private static final String[] MONSTER_TYPES = {"Aqua", "Beast", "Beast-Warrior", "Creator-God", "Cyberse", "Dinosaur", "Divine-Beast", "Dragon", "Fairy", "Fiend", "Fish", "Illusion", "Insect", "Machine", "Plant", "Psychic", "Pyro", "Reptile", "Rock", "Sea Serpent", "Spellcaster", "Thunder", "Warrior", "Winged Beast", "Wyrm", "Zombie"};

    //Components used for home screen
    private ImagePanel homePnl;
    private Dimension homeSize;
    private Container contentPane;
    private SpringLayout layout;
    private SpringLayout homeLayout;
    private JLabel ygoSymbol;
    private JLabel dbTitle;
    private JButton startButton;
    private JButton creditsButton;
    private JButton exitButton;
    private ImageIcon ygoLogo;
    private JTabbedPane dbTabPane;
    // Components used for TabbedPane
    private ImagePanel srchPnl;
    private ImagePanel resPnl;
    private SpringLayout searchLayout;
    private SpringLayout resultsLayout;
    //Components used for search tab in TabbedPane

    //BG Images
    private BufferedImage homeImg; //BufferedImage to store the background for the home screen
    private BufferedImage searchImg; //BufferedImage to store the background for the search tab
    private BufferedImage resultsImg; //BufferedImage to store the background for the results tab

    //Private Variables
    private List<String> cardRes;

    //Fonts
    private Font cardNameFont; //Font used for the name's of cards
    private Font cardEffectFont; //Font used for the effect's of cards
    private Font cardDescFont; //Font used for the description's of normal monsters
    private Font cardDetailsFont; //Font used for ATK/DEF/Type of monsters
    private Font cardSetFont; //Font used for the sets cards appear in (temporary)
    private Font titleFont; //Basic database font used for the title
    private Font mainFont; //Default font used throughout the database
    
    public YGDB() {
        //Load the fonts and then register them with the GraphicsEnvironment
        fontLoading();
        fontRegistration();
        //Get the size of the screen
        homeSize = Toolkit.getDefaultToolkit().getScreenSize();
        //Initialise some default settings
        this.setTitle("YGDB");
        this.setSize((int)homeSize.getWidth(),(int)homeSize.getHeight());
        this.setUndecorated(true);
        //Get contentPane, create a SpringLayout, and add it to contentPane
        contentPane = this.getContentPane();
        layout = new SpringLayout();
        contentPane.setLayout(layout);
        //Create and add the home panel
        createHomePanel();
        contentPane.add(homePnl);
        //Create and set the layout of the home panel
        createNewHomeLayout();
        //Create the YGO Logo
//        createLogoLabel();
        //Create the JLabel for the database title
//        createTitleLabel();
        //Create the start button
//        createStartButton();
        //Create the credits/about button
//        createCreditsButton();
        //Create the credits/about button
//        createExitButton();
        //Setup the locations of the components and the UI design
//        setupUi();
        //Add components
//        buildUi();

        /*REMEMBER TO MOVE ALL OF THIS INTO ANOTHER FUNCTION THAT WILL BE RUN AFTER THE UPDATE FUNCTION ON THE START BUTTON */
        /* SEARCH PANEL */
        try {
            dbTabPane = new JTabbedPane();
            dbTabPane.setOpaque(false);
            homePnl.add(dbTabPane);

            searchImg = ImageIO.read(new File("img/system/searchBg.jpg"));

            srchPnl = new ImagePanel(searchImg);
            searchLayout = new SpringLayout();
            srchPnl.setLayout(searchLayout);

            dbTabPane.addTab("Search", srchPnl);
            dbTabPane.setIconAt(0, new ImageIcon("img/system/srchIcon.png"));
            dbTabPane.setFont(mainFont.deriveFont(10f));

            //Work with search panel
            /* ----- NAME INPUT ----- */
            //Label for name box
            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(mainFont.deriveFont(22f));
            nameLabel.setForeground(Color.WHITE);
            //Textfield for name input
            JTextField monsName = new JTextField(40); //Set the width of the text field (40 is large enough for now)
            monsName.setFont(cardNameFont.deriveFont(22f));
            monsName.setToolTipText("Enter the full name or part of a name of a Yu-Gi-Oh! card.");
            //UI Constraints
            searchLayout.putConstraint(SpringLayout.WEST, nameLabel, 5, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, nameLabel, 5, SpringLayout.NORTH, srchPnl);
            searchLayout.putConstraint(SpringLayout.WEST, monsName, (int)(nameLabel.getPreferredSize().getWidth() + 10), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, monsName, 5, SpringLayout.NORTH, srchPnl);
            //Adding
            srchPnl.add(nameLabel);
            srchPnl.add(monsName);
            /* ----- SEARCH TYPE ----- */
            //Label for search type
            JLabel searchLabel = new JLabel("Search Type:");
            searchLabel.setFont(mainFont.deriveFont(22f));
            searchLabel.setForeground(Color.WHITE);
            //Radio Button for fuzzy search
            JRadioButton fName = new JRadioButton("Fuzzy", false);
            fName.setOpaque(false);
            fName.setFont(mainFont.deriveFont(22f));
            fName.setForeground(Color.WHITE);
            fName.setToolTipText("<html>Choose to preform a fuzzy search.<br>Retrieves all cards that have the inputted word(s) in their name.</html>");
            fName.setFocusPainted(false);
            //Radio Button for exact search
            JRadioButton eName = new JRadioButton("Exact", true);
            eName.setOpaque(false);
            eName.setFont(mainFont.deriveFont(22f));
            eName.setForeground(Color.WHITE);
            eName.setToolTipText("<html>Choose to perform an exact search.<br>Retrieves the card whose name matches the inputted text.</html>");
            eName.setFocusPainted(false);
            //VertSeparator for the search type
            JSeparator srchTypeSep = new JSeparator(JSeparator.VERTICAL);
            srchTypeSep.setPreferredSize(new Dimension(5, (int)searchLabel.getPreferredSize().getHeight()));
            //Constraints
            searchLayout.putConstraint(SpringLayout.WEST, searchLabel, 5, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, searchLabel, (int)(nameLabel.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, nameLabel);
            
            searchLayout.putConstraint(SpringLayout.WEST, eName, (int)(searchLabel.getPreferredSize().getWidth() + 10), SpringLayout.WEST, searchLabel);
            searchLayout.putConstraint(SpringLayout.NORTH, eName, (int)(nameLabel.getPreferredSize().getHeight() + 1), SpringLayout.NORTH, nameLabel);
            
            searchLayout.putConstraint(SpringLayout.WEST, fName, (int)(srchTypeSep.getPreferredSize().getWidth() + 1), SpringLayout.WEST, srchTypeSep);
            searchLayout.putConstraint(SpringLayout.NORTH, fName, (int)(nameLabel.getPreferredSize().getHeight() + 1), SpringLayout.NORTH, nameLabel);
            
            searchLayout.putConstraint(SpringLayout.WEST, srchTypeSep, (int)(eName.getPreferredSize().getWidth() + 1), SpringLayout.WEST, eName);
            searchLayout.putConstraint(SpringLayout.NORTH, srchTypeSep, (int)(nameLabel.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, nameLabel);            
            //Adding
            srchPnl.add(eName);
            srchPnl.add(fName);
            srchPnl.add(searchLabel);
            srchPnl.add(srchTypeSep);
            //Tooltip formatting            
            UIManager.put("ToolTip.font", mainFont.deriveFont(12f));
            UIManager.put("ToolTip.foreground", new Color(218, 165, 32));
            UIManager.put("ToolTip.background", Color.BLACK);
            //Make SearchType button group
            ButtonGroup searchTypeButtonGroup = new ButtonGroup();
            searchTypeButtonGroup.add(fName);
            searchTypeButtonGroup.add(eName);

            /* ----- ATTRIBUTE SELECTION ----- */
            //Horizontal Separator from Name Search
            JSeparator horizSepOne = new JSeparator(JSeparator.HORIZONTAL);
            horizSepOne.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            horizSepOne.setBackground(Color.BLACK);
            horizSepOne.setForeground(Color.RED);
            //Label attribute section
            JLabel attrLabel = new JLabel("Attribute:");
            attrLabel.setFont(mainFont.deriveFont(22f));
            attrLabel.setForeground(Color.WHITE);
            //Checkboxes per attribute
            //DARK CHECKBOX
            JCheckBox dark = new JCheckBox();
            dark.setOpaque(false);
            JLabel darkLab = new JLabel("Dark", new ImageIcon("img/attributes/darkOpt.png"), JLabel.LEFT);
            darkLab.setFont(cardSetFont.deriveFont(22f));
            darkLab.setForeground(Color.WHITE);
            dark.setMnemonic(KeyEvent.VK_D);
            dark.setSelected(false);
            dark.setToolTipText("<html>Select to search for the <i>DARK</i> attribute.<br>Shortcut: Alt+D.</html>");
            //DIVINE CHECKBOX
            JCheckBox divine = new JCheckBox();
            divine.setOpaque(false);
            JLabel divineLab = new JLabel("Divine", new ImageIcon("img/attributes/divineOpt.png"), JLabel.LEFT);
            divineLab.setFont(cardSetFont.deriveFont(22f));
            divineLab.setForeground(Color.WHITE);
            divine.setMnemonic(KeyEvent.VK_I);
            divine.setSelected(false);
            divine.setToolTipText("<html>Select to search for the <i>DIVINE</i> attribute.<br>Shortcut: Alt+I.</html>");
            //EARTH CHECKBOX
            JCheckBox earth = new JCheckBox();
            earth.setOpaque(false);
            JLabel earthLab = new JLabel("Earth", new ImageIcon("img/attributes/earthOpt.png"), JLabel.LEFT);
            earthLab.setFont(cardSetFont.deriveFont(22f));
            earthLab.setForeground(Color.WHITE);
            earth.setMnemonic(KeyEvent.VK_E);
            earth.setSelected(false);
            earth.setToolTipText("<html>Select to search for the <i>EARTH</i> attribute.<br>Shortcut: Alt+E.</html>");
            //FIRE CHECKBOX
            JCheckBox fire = new JCheckBox();
            fire.setOpaque(false);
            JLabel fireLab = new JLabel("Fire", new ImageIcon("img/attributes/fireOpt.png"), JLabel.LEFT);
            fireLab.setFont(cardSetFont.deriveFont(22f));
            fireLab.setForeground(Color.WHITE);
            fire.setMnemonic(KeyEvent.VK_F);
            fire.setSelected(false);
            fire.setToolTipText("<html>Select to search for the <i>FIRE</i> attribute.<br>Shortcut: Alt+F.</html>");
            //LIGHT CHECKBOX
            JCheckBox light = new JCheckBox();
            light.setOpaque(false);
            JLabel lightLab = new JLabel("Light", new ImageIcon("img/attributes/lightOpt.png"), JLabel.LEFT);
            lightLab.setFont(cardSetFont.deriveFont(22f));
            lightLab.setForeground(Color.WHITE);
            light.setMnemonic(KeyEvent.VK_L);
            light.setSelected(false);
            light.setToolTipText("<html>Select to search for the <i>LIGHT</i> attribute.<br>Shortcut: Alt+L.</html>");
            //SPELL CHECKBOX
            JCheckBox spell = new JCheckBox();
            spell.setOpaque(false);
            JLabel spellLab = new JLabel("Spell", new ImageIcon("img/attributes/spellOpt.png"), JLabel.LEFT);
            spellLab.setFont(cardSetFont.deriveFont(22f));
            spellLab.setForeground(Color.WHITE);
            spell.setMnemonic(KeyEvent.VK_S);
            spell.setSelected(false);
            spell.setToolTipText("<html>Select to search for the <i>SPELL</i> attribute.<br>Shortcut: Alt+S.</html>");
            //TRAP CHECKBOX
            JCheckBox trap = new JCheckBox();
            trap.setOpaque(false);
            JLabel trapLab = new JLabel("Trap", new ImageIcon("img/attributes/trapOpt.png"), JLabel.LEFT);
            trapLab.setFont(cardSetFont.deriveFont(22f));
            trapLab.setForeground(Color.WHITE);
            trap.setMnemonic(KeyEvent.VK_T);
            trap.setSelected(false);
            trap.setToolTipText("<html>Select to search for the <i>TRAP</i> attribute.<br>Shortcut: Alt+T.</html>");
            //WATER CHECKBOX
            JCheckBox water = new JCheckBox();
            water.setOpaque(false);
            JLabel waterLab = new JLabel("Water", new ImageIcon("img/attributes/waterOpt.png"), JLabel.LEFT);
            waterLab.setFont(cardSetFont.deriveFont(22f));
            waterLab.setForeground(Color.WHITE);
            water.setMnemonic(KeyEvent.VK_W);
            water.setSelected(false);
            water.setToolTipText("<html>Select to search for the <i>WATER</i> attribute.<br>Shortcut: Alt+W.</html>");
            //WIND CHECKBOX
            JCheckBox wind = new JCheckBox();
            wind.setOpaque(false);
            JLabel windLab = new JLabel("Wind", new ImageIcon("img/attributes/windOpt.png"), JLabel.LEFT);
            windLab.setFont(cardSetFont.deriveFont(22f));
            windLab.setForeground(Color.WHITE);
            wind.setMnemonic(KeyEvent.VK_N);
            wind.setSelected(false);
            wind.setToolTipText("<html>Select to search for the <i>WIND</i> attribute.<br>Shortcut: Alt+N.</html>");
            //Adding
            srchPnl.add(attrLabel);
            srchPnl.add(dark);
            srchPnl.add(darkLab);
            srchPnl.add(horizSepOne);
            srchPnl.add(divine);
            srchPnl.add(divineLab);
            srchPnl.add(earth);
            srchPnl.add(earthLab);
            srchPnl.add(fire);
            srchPnl.add(fireLab);
            srchPnl.add(light);
            srchPnl.add(lightLab);
            srchPnl.add(spell);
            srchPnl.add(spellLab);
            srchPnl.add(trap);
            srchPnl.add(trapLab);
            srchPnl.add(water);
            srchPnl.add(waterLab);
            srchPnl.add(wind);
            srchPnl.add(windLab);
            //UI Layout
            searchLayout.putConstraint(SpringLayout.WEST, horizSepOne, 0, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, horizSepOne, (int)(searchLabel.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, searchLabel);
            searchLayout.putConstraint(SpringLayout.WEST, attrLabel, 5, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, attrLabel, (int)(horizSepOne.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepOne);

            searchLayout.putConstraint(SpringLayout.WEST, dark, 25, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, dark, (int)(attrLabel.getPreferredSize().getHeight() + darkLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, attrLabel);
            searchLayout.putConstraint(SpringLayout.WEST, darkLab, (int)(dark.getPreferredSize().getWidth() + 1), SpringLayout.WEST, dark);
            searchLayout.putConstraint(SpringLayout.NORTH, darkLab, (int)(dark.getPreferredSize().getHeight()/2 - darkLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, dark);

            searchLayout.putConstraint(SpringLayout.WEST, divine, (int)(darkLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, darkLab);
            searchLayout.putConstraint(SpringLayout.NORTH, divine, (int)(attrLabel.getPreferredSize().getHeight() + divineLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, attrLabel);
            searchLayout.putConstraint(SpringLayout.WEST, divineLab, (int)(divine.getPreferredSize().getWidth() + 1), SpringLayout.WEST, divine);
            searchLayout.putConstraint(SpringLayout.NORTH, divineLab, (int)(divine.getPreferredSize().getHeight()/2 - divineLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, divine);

            searchLayout.putConstraint(SpringLayout.WEST, earth, (int)(divineLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, divineLab);
            searchLayout.putConstraint(SpringLayout.NORTH, earth, (int)(attrLabel.getPreferredSize().getHeight() + earthLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, attrLabel);
            searchLayout.putConstraint(SpringLayout.WEST, earthLab, (int)(earth.getPreferredSize().getWidth() + 1), SpringLayout.WEST, earth);
            searchLayout.putConstraint(SpringLayout.NORTH, earthLab, (int)(earth.getPreferredSize().getHeight()/2 - earthLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, earth);

            searchLayout.putConstraint(SpringLayout.WEST, fire, 0, SpringLayout.WEST, dark);
            searchLayout.putConstraint(SpringLayout.NORTH, fire, (int)(darkLab.getPreferredSize().getHeight() + fireLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, dark);
            searchLayout.putConstraint(SpringLayout.WEST, fireLab, (int)(fire.getPreferredSize().getWidth() + 1), SpringLayout.WEST, fire);
            searchLayout.putConstraint(SpringLayout.NORTH, fireLab, (int)(fire.getPreferredSize().getHeight()/2 - fireLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, fire);

            searchLayout.putConstraint(SpringLayout.WEST, light, (int)(darkLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, fireLab);
            searchLayout.putConstraint(SpringLayout.NORTH, light, (int)(divineLab.getPreferredSize().getHeight() + lightLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, divine);
            searchLayout.putConstraint(SpringLayout.WEST, lightLab, (int)(light.getPreferredSize().getWidth() + 1), SpringLayout.WEST, light);
            searchLayout.putConstraint(SpringLayout.NORTH, lightLab, (int)(light.getPreferredSize().getHeight()/2 - lightLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, light);
            
            searchLayout.putConstraint(SpringLayout.WEST, spell, (int)(divineLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, lightLab);
            searchLayout.putConstraint(SpringLayout.NORTH, spell, (int)(earthLab.getPreferredSize().getHeight() + spellLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, earth);
            searchLayout.putConstraint(SpringLayout.WEST, spellLab, (int)(spell.getPreferredSize().getWidth() + 1), SpringLayout.WEST, spell);
            searchLayout.putConstraint(SpringLayout.NORTH, spellLab, (int)(spell.getPreferredSize().getHeight()/2 - spellLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, spell);

            searchLayout.putConstraint(SpringLayout.WEST, trap, 0, SpringLayout.WEST, fire);
            searchLayout.putConstraint(SpringLayout.NORTH, trap, (int)(fireLab.getPreferredSize().getHeight() + trapLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, fire);
            searchLayout.putConstraint(SpringLayout.WEST, trapLab, (int)(trap.getPreferredSize().getWidth() + 1), SpringLayout.WEST, trap);
            searchLayout.putConstraint(SpringLayout.NORTH, trapLab, (int)(trap.getPreferredSize().getHeight()/2 - trapLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, trap);

            searchLayout.putConstraint(SpringLayout.WEST, water, (int)(darkLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, trapLab);
            searchLayout.putConstraint(SpringLayout.NORTH, water, (int)(lightLab.getPreferredSize().getHeight() + waterLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, light);
            searchLayout.putConstraint(SpringLayout.WEST, waterLab, (int)(water.getPreferredSize().getWidth() + 1), SpringLayout.WEST, water);
            searchLayout.putConstraint(SpringLayout.NORTH, waterLab, (int)(water.getPreferredSize().getHeight()/2 - waterLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, water);

            searchLayout.putConstraint(SpringLayout.WEST, wind, (int)(divineLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, waterLab);
            searchLayout.putConstraint(SpringLayout.NORTH, wind, (int)(spellLab.getPreferredSize().getHeight() + windLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, spell);
            searchLayout.putConstraint(SpringLayout.WEST, windLab, (int)(wind.getPreferredSize().getWidth() + 1), SpringLayout.WEST, wind);
            searchLayout.putConstraint(SpringLayout.NORTH, windLab, (int)(wind.getPreferredSize().getHeight()/2 - windLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, wind);
        
            /* ----- LEVEL/RANK SELECTION ----- */
            //Horizontal Separator from Attribute Search
            JSeparator horizSepTwo = new JSeparator(JSeparator.HORIZONTAL);
            horizSepTwo.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            horizSepTwo.setBackground(Color.BLACK);
            horizSepTwo.setForeground(Color.RED);
            //Label for level section
            JLabel lvlRnkLabel = new JLabel("Level/Rank:");
            lvlRnkLabel.setFont(mainFont.deriveFont(22f));
            lvlRnkLabel.setForeground(Color.WHITE);
            //LEVEL/RANK SPINNER
            SpinnerModel levelLimit = new SpinnerNumberModel(-1, -1, 13, 1);
            JSpinner lvlRnk = new JSpinner(levelLimit);
            lvlRnk.setFont(cardDetailsFont.deriveFont(22f));
            lvlRnk.setToolTipText("<html>Search by Level/Rank (0-13).<br>Select a level of -1 to exclude from search.</html>");
            //Radio Button for less than
            JRadioButton lvlLt = new JRadioButton("<", false);
            lvlLt.setOpaque(false);
            lvlLt.setFont(mainFont.deriveFont(22f));
            lvlLt.setForeground(Color.WHITE);
            lvlLt.setToolTipText("<html>Retrieves cards whose levels are less than the chosen value.</html>");
            lvlLt.setFocusPainted(false);
            //Radio Button for less than or equal to
            JRadioButton lvlLte = new JRadioButton("<=", false);
            lvlLte.setOpaque(false);
            lvlLte.setFont(mainFont.deriveFont(22f));
            lvlLte.setForeground(Color.WHITE);
            lvlLte.setToolTipText("<html>Retrieves cards whose levels are less than or equal to the chosen value.</html>");
            lvlLte.setFocusPainted(false);
            //Radio Button for equal to
            JRadioButton lvlEq = new JRadioButton("==", true);
            lvlEq.setOpaque(false);
            lvlEq.setFont(mainFont.deriveFont(22f));
            lvlEq.setForeground(Color.WHITE);
            lvlEq.setToolTipText("<html>Retrieves cards whose levels are equal to the chosen value.</html>");
            lvlEq.setFocusPainted(false);
            //Radio Button for greater than or equal to
            JRadioButton lvlGte = new JRadioButton(">=", false);
            lvlGte.setOpaque(false);
            lvlGte.setFont(mainFont.deriveFont(22f));
            lvlGte.setForeground(Color.WHITE);
            lvlGte.setToolTipText("<html>Retrieves cards whose levels are greater than or equal to the chosen value.</html>");
            lvlGte.setFocusPainted(false);
            //Radio Button for greater than
            JRadioButton lvlGt = new JRadioButton(">", false);
            lvlGt.setOpaque(false);
            lvlGt.setFont(mainFont.deriveFont(22f));
            lvlGt.setForeground(Color.WHITE);
            lvlGt.setToolTipText("<html>Retrieves cards whose levels are greater than the chosen value.</html>");
            lvlGt.setFocusPainted(false);
            //Adding
            srchPnl.add(lvlRnkLabel);
            srchPnl.add(horizSepTwo);
            srchPnl.add(lvlRnk);
            srchPnl.add(lvlLt);
            srchPnl.add(lvlLte);
            srchPnl.add(lvlEq);
            srchPnl.add(lvlGte);
            srchPnl.add(lvlGt);
            //UI Layout
            searchLayout.putConstraint(SpringLayout.WEST, horizSepTwo, 0, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, horizSepTwo, (int)(waterLab.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, waterLab);
            searchLayout.putConstraint(SpringLayout.WEST, lvlRnkLabel, 5, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, lvlRnkLabel, (int)(horizSepTwo.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepTwo);

            searchLayout.putConstraint(SpringLayout.WEST, lvlRnk, (int)(horizSepTwo.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, lvlRnk, (int)(lvlRnkLabel.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnkLabel);

            searchLayout.putConstraint(SpringLayout.WEST, lvlLt, (int)(horizSepTwo.getPreferredSize().getWidth()/6), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, lvlLt, (int)(lvlRnk.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnk);

            searchLayout.putConstraint(SpringLayout.WEST, lvlLte, (int)(horizSepTwo.getPreferredSize().getWidth()/3), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, lvlLte, (int)(lvlRnk.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnk);

            searchLayout.putConstraint(SpringLayout.WEST, lvlEq, (int)(horizSepTwo.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, lvlEq, (int)(lvlRnk.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnk);

            searchLayout.putConstraint(SpringLayout.WEST, lvlGte, (int)(horizSepTwo.getPreferredSize().getWidth() * 2/3), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, lvlGte, (int)(lvlRnk.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnk);

            searchLayout.putConstraint(SpringLayout.WEST, lvlGt, (int)(horizSepTwo.getPreferredSize().getWidth() * 5/6), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, lvlGt, (int)(lvlRnk.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnk);
            //Make lvlRnkComparison button group
            ButtonGroup lvlRnkComparisonButtonGroup = new ButtonGroup();
            lvlRnkComparisonButtonGroup.add(lvlLt);
            lvlRnkComparisonButtonGroup.add(lvlLte);
            lvlRnkComparisonButtonGroup.add(lvlEq);
            lvlRnkComparisonButtonGroup.add(lvlGte);
            lvlRnkComparisonButtonGroup.add(lvlGt);

            /* ----- LINK RATING SELECTION ----- */
            //Horizontal Separator from Level Search
            JSeparator horizSepThree = new JSeparator(JSeparator.HORIZONTAL);
            horizSepThree.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            horizSepThree.setBackground(Color.BLACK);
            horizSepThree.setForeground(Color.RED);
            //Label for link rating section
            JLabel linkRateLabel = new JLabel("Link Rating:");
            linkRateLabel.setFont(mainFont.deriveFont(22f));
            linkRateLabel.setForeground(Color.WHITE);
            //LINK RATING SPINNER
            SpinnerModel linkRateLimit = new SpinnerNumberModel(0, 0, 8, 1);
            JSpinner linkRate = new JSpinner(linkRateLimit);
            linkRate.setFont(cardDetailsFont.deriveFont(22f));
            linkRate.setToolTipText("<html>Search by Link Rating (1-8).<br>Select a rating of 0 to exclude from search.</html>");
            Component linkEditor = linkRate.getEditor();
            JFormattedTextField linkJftf = ((JSpinner.DefaultEditor) linkEditor).getTextField();
            linkJftf.setColumns(2);
            //Radio Button for less than
            JRadioButton linkRateLt = new JRadioButton("<", false);
            linkRateLt.setOpaque(false);
            linkRateLt.setFont(mainFont.deriveFont(22f));
            linkRateLt.setForeground(Color.WHITE);
            linkRateLt.setToolTipText("<html>Retrieves cards whose link ratings are less than the chosen value.</html>");
            linkRateLt.setFocusPainted(false);
            //Radio Button for less than or equal to
            JRadioButton linkRateLte = new JRadioButton("<=", false);
            linkRateLte.setOpaque(false);
            linkRateLte.setFont(mainFont.deriveFont(22f));
            linkRateLte.setForeground(Color.WHITE);
            linkRateLte.setToolTipText("<html>Retrieves cards whose link ratings are less than or equal to the chosen value.</html>");
            linkRateLte.setFocusPainted(false);
            //Radio Button for equal to
            JRadioButton linkRateEq = new JRadioButton("==", true);
            linkRateEq.setOpaque(false);
            linkRateEq.setFont(mainFont.deriveFont(22f));
            linkRateEq.setForeground(Color.WHITE);
            linkRateEq.setToolTipText("<html>Retrieves cards whose link ratings are equal to the chosen value.</html>");
            linkRateEq.setFocusPainted(false);
            //Radio Button for greater than or equal to
            JRadioButton linkRateGte = new JRadioButton(">=", false);
            linkRateGte.setOpaque(false);
            linkRateGte.setFont(mainFont.deriveFont(22f));
            linkRateGte.setForeground(Color.WHITE);
            linkRateGte.setToolTipText("<html>Retrieves cards whose link ratings are greater than or equal to the chosen value.</html>");
            linkRateGte.setFocusPainted(false);
            //Radio Button for greater than
            JRadioButton linkRateGt = new JRadioButton(">", false);
            linkRateGt.setOpaque(false);
            linkRateGt.setFont(mainFont.deriveFont(22f));
            linkRateGt.setForeground(Color.WHITE);
            linkRateGt.setToolTipText("<html>Retrieves cards whose link ratings are greater than the chosen value.</html>");
            linkRateGt.setFocusPainted(false);
            //Adding
            srchPnl.add(linkRateLabel);
            srchPnl.add(horizSepThree);
            srchPnl.add(linkRate);
            srchPnl.add(linkRateLt);
            srchPnl.add(linkRateLte);
            srchPnl.add(linkRateEq);
            srchPnl.add(linkRateGte);
            srchPnl.add(linkRateGt);
            //UI Layout
            searchLayout.putConstraint(SpringLayout.WEST, horizSepThree, 0, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, horizSepThree, (int)(lvlEq.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, lvlEq);
            searchLayout.putConstraint(SpringLayout.WEST, linkRateLabel, 5, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, linkRateLabel, (int)(horizSepThree.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepThree);

            searchLayout.putConstraint(SpringLayout.WEST, linkRate, (int)(horizSepThree.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, linkRate, (int)(linkRateLabel.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRateLabel);

            searchLayout.putConstraint(SpringLayout.WEST, linkRateLt, (int)(horizSepThree.getPreferredSize().getWidth()/6), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, linkRateLt, (int)(linkRate.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRate);

            searchLayout.putConstraint(SpringLayout.WEST, linkRateLte, (int)(horizSepThree.getPreferredSize().getWidth()/3), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, linkRateLte, (int)(linkRate.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRate);

            searchLayout.putConstraint(SpringLayout.WEST, linkRateEq, (int)(horizSepThree.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, linkRateEq, (int)(linkRate.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRate);

            searchLayout.putConstraint(SpringLayout.WEST, linkRateGte, (int)(horizSepThree.getPreferredSize().getWidth() * 2/3), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, linkRateGte, (int)(linkRate.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRate);

            searchLayout.putConstraint(SpringLayout.WEST, linkRateGt, (int)(horizSepThree.getPreferredSize().getWidth() * 5/6), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, linkRateGt, (int)(linkRate.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRate);
            //Make linkRateComparison button group
            ButtonGroup linkRateComparisonButtonGroup = new ButtonGroup();
            linkRateComparisonButtonGroup.add(linkRateLt);
            linkRateComparisonButtonGroup.add(linkRateLte);
            linkRateComparisonButtonGroup.add(linkRateEq);
            linkRateComparisonButtonGroup.add(linkRateGte);
            linkRateComparisonButtonGroup.add(linkRateGt);

            /* ----- PENDULUM SCALE SELECTION ----- */
            //Horizontal Separator from Link Rating Search
            JSeparator horizSepFour = new JSeparator(JSeparator.HORIZONTAL);
            horizSepFour.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            horizSepFour.setBackground(Color.BLACK);
            horizSepFour.setForeground(Color.RED);
            //Label for link rating section
            JLabel pendScaleLabel = new JLabel("Pendulum Scale:");
            pendScaleLabel.setFont(mainFont.deriveFont(22f));
            pendScaleLabel.setForeground(Color.WHITE);
            //LINK RATING SPINNER
            SpinnerModel pendScaleLimit = new SpinnerNumberModel(-1, -1, 13, 1);
            JSpinner pendScale = new JSpinner(pendScaleLimit);
            pendScale.setFont(cardDetailsFont.deriveFont(22f));
            pendScale.setToolTipText("<html>Search by Pendulum Scale (0-13).<br>Select a scale of -1 to exclude from search.</html>");
            //Radio Button for less than
            JRadioButton pendScaleLt = new JRadioButton("<", false);
            pendScaleLt.setOpaque(false);
            pendScaleLt.setFont(mainFont.deriveFont(22f));
            pendScaleLt.setForeground(Color.WHITE);
            pendScaleLt.setToolTipText("<html>Retrieves cards whose pendulum scales are less than the chosen value.</html>");
            pendScaleLt.setFocusPainted(false);
            //Radio Button for less than or equal to
            JRadioButton pendScaleLte = new JRadioButton("<=", false);
            pendScaleLte.setOpaque(false);
            pendScaleLte.setFont(mainFont.deriveFont(22f));
            pendScaleLte.setForeground(Color.WHITE);
            pendScaleLte.setToolTipText("<html>Retrieves cards whose pendulum scales are less than or equal to the chosen value.</html>");
            pendScaleLte.setFocusPainted(false);
            //Radio Button for equal to
            JRadioButton pendScaleEq = new JRadioButton("==", true);
            pendScaleEq.setOpaque(false);
            pendScaleEq.setFont(mainFont.deriveFont(22f));
            pendScaleEq.setForeground(Color.WHITE);
            pendScaleEq.setToolTipText("<html>Retrieves cards whose pendulum scales are equal to the chosen value.</html>");
            pendScaleEq.setFocusPainted(false);
            //Radio Button for greater than or equal to
            JRadioButton pendScaleGte = new JRadioButton(">=", false);
            pendScaleGte.setOpaque(false);
            pendScaleGte.setFont(mainFont.deriveFont(22f));
            pendScaleGte.setForeground(Color.WHITE);
            pendScaleGte.setToolTipText("<html>Retrieves cards whose pendulum scales are greater than or equal to the chosen value.</html>");
            pendScaleGte.setFocusPainted(false);
            //Radio Button for greater than
            JRadioButton pendScaleGt = new JRadioButton(">", false);
            pendScaleGt.setOpaque(false);
            pendScaleGt.setFont(mainFont.deriveFont(22f));
            pendScaleGt.setForeground(Color.WHITE);
            pendScaleGt.setToolTipText("<html>Retrieves cards whose pendulum scales are greater than the chosen value.</html>");
            pendScaleGt.setFocusPainted(false);
            //Adding
            srchPnl.add(pendScaleLabel);
            srchPnl.add(horizSepFour);
            srchPnl.add(pendScale);
            srchPnl.add(pendScaleLt);
            srchPnl.add(pendScaleLte);
            srchPnl.add(pendScaleEq);
            srchPnl.add(pendScaleGte);
            srchPnl.add(pendScaleGt);
            //UI Layout
            searchLayout.putConstraint(SpringLayout.WEST, horizSepFour, 0, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, horizSepFour, (int)(linkRateEq.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, linkRateEq);

            searchLayout.putConstraint(SpringLayout.WEST, pendScaleLabel, 5, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, pendScaleLabel, (int)(horizSepFour.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepFour);

            searchLayout.putConstraint(SpringLayout.WEST, pendScale, (int)(horizSepFour.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, pendScale, (int)(pendScaleLabel.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScaleLabel);

            searchLayout.putConstraint(SpringLayout.WEST, pendScaleLt, (int)(horizSepFour.getPreferredSize().getWidth()/6), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, pendScaleLt, (int)(pendScale.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScale);

            searchLayout.putConstraint(SpringLayout.WEST, pendScaleLte, (int)(horizSepFour.getPreferredSize().getWidth()/3), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, pendScaleLte, (int)(pendScale.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScale);

            searchLayout.putConstraint(SpringLayout.WEST, pendScaleEq, (int)(horizSepFour.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, pendScaleEq, (int)(pendScale.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScale);

            searchLayout.putConstraint(SpringLayout.WEST, pendScaleGte, (int)(horizSepFour.getPreferredSize().getWidth() * 2/3), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, pendScaleGte, (int)(pendScale.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScale);

            searchLayout.putConstraint(SpringLayout.WEST, pendScaleGt, (int)(horizSepFour.getPreferredSize().getWidth() * 5/6), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, pendScaleGt, (int)(pendScale.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScale);
            //Make linkRateComparison button group
            ButtonGroup pendScaleComparisonButtonGroup = new ButtonGroup();
            pendScaleComparisonButtonGroup.add(pendScaleLt);
            pendScaleComparisonButtonGroup.add(pendScaleLte);
            pendScaleComparisonButtonGroup.add(pendScaleEq);
            pendScaleComparisonButtonGroup.add(pendScaleGte);
            pendScaleComparisonButtonGroup.add(pendScaleGt);

            /* ----- LINK MARKER SELECTION ----- */
            //Label for link rating section
            JLabel linkArrowLabel = new JLabel("Link Arrows:");
            linkArrowLabel.setFont(mainFont.deriveFont(22f));
            linkArrowLabel.setForeground(Color.WHITE);
            //Up Link Arrow
            JCheckBox uArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkuoff.png"));
            uArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkuon.png"));
            uArrow.setToolTipText("Select to search for cards that have a top Link Arrow.");
            uArrow.setOpaque(false);
            //Up-Right Link Arrow
            JCheckBox uRArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkuroff.png"));
            uRArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkuron.png"));
            uRArrow.setToolTipText("Select to search for cards that have a top-right Link Arrow.");
            uRArrow.setOpaque(false);
            //Up-Left Link Arrow
            JCheckBox uLArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkuloff.png"));
            uLArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkulon.png"));
            uLArrow.setToolTipText("Select to search for cards that have a top-left Link Arrow.");
            uLArrow.setOpaque(false);
            //Left Link Arrow
            JCheckBox lArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkloff.png"));
            lArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linklon.png"));
            lArrow.setToolTipText("Select to search for cards that have a left Link Arrow.");
            lArrow.setOpaque(false);
            //Right Link Arrow
            JCheckBox rArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkroff.png"));
            rArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkron.png"));
            rArrow.setToolTipText("Select to search for cards that have a right Link Arrow.");
            rArrow.setOpaque(false);
            //Bottom Link Arrow
            JCheckBox bArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkdoff.png"));
            bArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkdon.png"));
            bArrow.setToolTipText("Select to search for cards that have a bottom Link Arrow.");
            bArrow.setOpaque(false);
            //Up-Right Link Arrow
            JCheckBox bRArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkdroff.png"));
            bRArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkdron.png"));
            bRArrow.setToolTipText("Select to search for cards that have a bottom-right Link Arrow.");
            bRArrow.setOpaque(false);
            //Up-Left Link Arrow
            JCheckBox bLArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkdloff.png"));
            bLArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkdlon.png"));
            bLArrow.setToolTipText("Select to search for cards that have a bottom-left Link Arrow.");
            bLArrow.setOpaque(false);
            //Adding
            srchPnl.add(linkArrowLabel);
            srchPnl.add(uArrow);
            srchPnl.add(uLArrow);
            srchPnl.add(uRArrow);
            srchPnl.add(rArrow);
            srchPnl.add(bRArrow);
            srchPnl.add(bArrow);
            srchPnl.add(bLArrow);
            srchPnl.add(lArrow);
            //UI Layout
            searchLayout.putConstraint(SpringLayout.WEST, linkArrowLabel, (int)(srchPnl.getPreferredSize().getWidth()/2 + 5), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, linkArrowLabel, 5, SpringLayout.NORTH, srchPnl);

            searchLayout.putConstraint(SpringLayout.WEST, uLArrow, (int)(linkArrowLabel.getPreferredSize().getWidth() + 5), SpringLayout.WEST, linkArrowLabel);
            searchLayout.putConstraint(SpringLayout.NORTH, uLArrow, 5, SpringLayout.NORTH, linkArrowLabel);

            searchLayout.putConstraint(SpringLayout.WEST, uArrow, (int)(uLArrow.getPreferredSize().getWidth() + 5), SpringLayout.WEST, uLArrow);
            searchLayout.putConstraint(SpringLayout.NORTH, uArrow, 5, SpringLayout.NORTH, uLArrow);

            searchLayout.putConstraint(SpringLayout.WEST, uRArrow, (int)(uArrow.getPreferredSize().getWidth() + 5), SpringLayout.WEST, uArrow);
            searchLayout.putConstraint(SpringLayout.NORTH, uRArrow, 0, SpringLayout.NORTH, uLArrow);

            searchLayout.putConstraint(SpringLayout.WEST, lArrow, (int)(linkArrowLabel.getPreferredSize().getWidth() + 5), SpringLayout.WEST, linkArrowLabel);
            searchLayout.putConstraint(SpringLayout.NORTH, lArrow, (int)(uLArrow.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, uLArrow);

            searchLayout.putConstraint(SpringLayout.WEST, rArrow, (int)(uArrow.getPreferredSize().getWidth() + uRArrow.getPreferredSize().getWidth()/2), SpringLayout.WEST, uArrow);
            searchLayout.putConstraint(SpringLayout.NORTH, rArrow, (int)(uRArrow.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, uRArrow);

            searchLayout.putConstraint(SpringLayout.WEST, bLArrow, (int)(linkArrowLabel.getPreferredSize().getWidth() + 5), SpringLayout.WEST, linkArrowLabel);
            searchLayout.putConstraint(SpringLayout.NORTH, bLArrow, (int)(uLArrow.getPreferredSize().getHeight() + lArrow.getPreferredSize().getHeight() + 10), SpringLayout.NORTH, uRArrow);

            searchLayout.putConstraint(SpringLayout.WEST, bArrow, (int)(bLArrow.getPreferredSize().getWidth() + 5), SpringLayout.WEST, bLArrow);
            searchLayout.putConstraint(SpringLayout.NORTH, bArrow, (int)(bLArrow.getPreferredSize().getHeight() - (bArrow.getPreferredSize().getHeight() + 5)), SpringLayout.NORTH, bLArrow);

            searchLayout.putConstraint(SpringLayout.WEST, bRArrow, (int)(bArrow.getPreferredSize().getWidth() + 5), SpringLayout.WEST, bArrow);
            searchLayout.putConstraint(SpringLayout.NORTH, bRArrow, 0, SpringLayout.NORTH, bLArrow);

            /* ----- TYPE SELECTION ----- */
            //Separator from Link Arrow Selection
            JSeparator horizSepFive = new JSeparator(JSeparator.HORIZONTAL);
            horizSepFive.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            horizSepFive.setBackground(Color.BLACK);
            horizSepFive.setForeground(Color.RED);
            //Type Label
            JLabel typeLabel = new JLabel("Type:");
            typeLabel.setFont(mainFont.deriveFont(22f));
            typeLabel.setForeground(Color.WHITE);
            //JList model
            DefaultListModel<String> monsDlm = new DefaultListModel<String>();
            for(int i = 0;i < MONSTER_TYPES.length; i++) {
                monsDlm.add(i, MONSTER_TYPES[i]);
            }
            //JList for monster types
            JList<String> monsType = new JList<String>(monsDlm);
            monsType.setLayoutOrientation(JList.VERTICAL_WRAP);
            monsType.setVisibleRowCount(13);
            monsType.setFont(cardDetailsFont.deriveFont(22f));
            monsType.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            monsType.setBackground(new Color(88, 17, 17));
            monsType.setForeground(Color.WHITE);
            //Scroller for JList
            JScrollPane monsTypeScroller = new JScrollPane(monsType);
            monsTypeScroller.setPreferredSize(new Dimension(300, 175));
            //Checkboxes per type
            //CONTINUOUS CHECKBOX
            JCheckBox continuous = new JCheckBox();
            continuous.setOpaque(false);
            JLabel contLab = new JLabel("Continuous", new ImageIcon("img/races/contOpt.png"), JLabel.LEFT);
            contLab.setFont(cardSetFont.deriveFont(22f));
            contLab.setForeground(Color.WHITE);
            continuous.setMnemonic(KeyEvent.VK_C);
            continuous.setSelected(false);
            continuous.setToolTipText("<html>Select to search for <i>CONTINUOUS</i> spells or traps.<br>Shortcut: Alt+C.</html>");
            //COUNTER CHECKBOX
            JCheckBox counter = new JCheckBox();
            counter.setOpaque(false);
            JLabel countLab = new JLabel("Counter", new ImageIcon("img/races/countOpt.png"), JLabel.LEFT);
            countLab.setFont(cardSetFont.deriveFont(22f));
            countLab.setForeground(Color.WHITE);
            counter.setMnemonic(KeyEvent.VK_U);
            counter.setSelected(false);
            counter.setToolTipText("<html>Select to search for <i>COUNTER</i> traps.<br>Shortcut: Alt+U.</html>");
            //EQUIP CHECKBOX
            JCheckBox equip = new JCheckBox();
            equip.setOpaque(false);
            JLabel equipLab = new JLabel("Equip", new ImageIcon("img/races/equipOpt.png"), JLabel.LEFT);
            equipLab.setFont(cardSetFont.deriveFont(22f));
            equipLab.setForeground(Color.WHITE);
            equip.setMnemonic(KeyEvent.VK_Q);
            equip.setSelected(false);
            equip.setToolTipText("<html>Select to search for <i>EQUIP</i> spells.<br>Shortcut: Alt+Q.</html>");
            //FIELD CHECKBOX
            JCheckBox field = new JCheckBox();
            field.setOpaque(false);
            JLabel fieldLab = new JLabel("Field", new ImageIcon("img/races/fieldOpt.png"), JLabel.LEFT);
            fieldLab.setFont(cardSetFont.deriveFont(22f));
            fieldLab.setForeground(Color.WHITE);
            field.setMnemonic(KeyEvent.VK_A);
            field.setSelected(false);
            field.setToolTipText("<html>Select to search for <i>FIELD</i> spells.<br>Shortcut: Alt+A.</html>");
            //QUICK-PLAY CHECKBOX
            JCheckBox quickPlay = new JCheckBox();
            quickPlay.setOpaque(false);
            JLabel qpLab = new JLabel("Quick-Play", new ImageIcon("img/races/qpOpt.png"), JLabel.LEFT);
            qpLab.setFont(cardSetFont.deriveFont(22f));
            qpLab.setForeground(Color.WHITE);
            quickPlay.setMnemonic(KeyEvent.VK_P);
            quickPlay.setSelected(false);
            quickPlay.setToolTipText("<html>Select to search for <i>QUICK-PLAY</i> spells.<br>Shortcut: Alt+Q.</html>");
            //RITUAL CHECKBOX
            JCheckBox ritual = new JCheckBox();
            ritual.setOpaque(false);
            JLabel ritLab = new JLabel("Ritual", new ImageIcon("img/races/ritOpt.png"), JLabel.LEFT);
            ritLab.setFont(cardSetFont.deriveFont(22f));
            ritLab.setForeground(Color.WHITE);
            ritual.setMnemonic(KeyEvent.VK_R);
            ritual.setSelected(false);
            ritual.setToolTipText("<html>Select to search for <i>RITUAL</i> spells.<br>Shortcut: Alt+R.</html>");
            //NORMAL CHECKBOX
            JCheckBox normal = new JCheckBox();
            normal.setOpaque(false);
            JLabel normLab = new JLabel("Normal");
            normLab.setFont(cardSetFont.deriveFont(22f));
            normLab.setForeground(Color.WHITE);
            normal.setMnemonic(KeyEvent.VK_O);
            normal.setSelected(false);
            normal.setToolTipText("<html>Select to search for <i>NORMAL</i> spells or traps.<br>Shortcut: Alt+O.</html>");
            //Adding
            srchPnl.add(horizSepFive);
            srchPnl.add(typeLabel);
            srchPnl.add(monsTypeScroller);
            srchPnl.add(continuous);
            srchPnl.add(contLab);
            srchPnl.add(counter);
            srchPnl.add(countLab);
            srchPnl.add(equip);
            srchPnl.add(equipLab);
            srchPnl.add(field);
            srchPnl.add(fieldLab);
            srchPnl.add(quickPlay);
            srchPnl.add(qpLab);
            srchPnl.add(ritual);
            srchPnl.add(ritLab);
            srchPnl.add(normal);
            srchPnl.add(normLab);
            //UI Layout
            searchLayout.putConstraint(SpringLayout.WEST, horizSepFive, (int)(srchPnl.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, horizSepFive, (int)(bLArrow.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, bLArrow);

            searchLayout.putConstraint(SpringLayout.WEST, typeLabel, (int)(srchPnl.getPreferredSize().getWidth()/2 + 5), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, typeLabel, (int)(horizSepFive.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepFive);

            searchLayout.putConstraint(SpringLayout.WEST, monsTypeScroller, (int)(srchPnl.getPreferredSize().getWidth()/2 + 25), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, monsTypeScroller, (int)(typeLabel.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, typeLabel);

            searchLayout.putConstraint(SpringLayout.WEST, continuous, (int)(monsTypeScroller.getPreferredSize().getWidth() + 15), SpringLayout.WEST, monsTypeScroller);
            searchLayout.putConstraint(SpringLayout.NORTH, continuous, (int)(typeLabel.getPreferredSize().getHeight() + contLab.getPreferredSize().getHeight()/2) - 5, SpringLayout.NORTH, typeLabel);
            searchLayout.putConstraint(SpringLayout.WEST, contLab, (int)(continuous.getPreferredSize().getWidth() + 1), SpringLayout.WEST, continuous);
            searchLayout.putConstraint(SpringLayout.NORTH, contLab, (int)(continuous.getPreferredSize().getHeight()/2 - contLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, continuous);

            searchLayout.putConstraint(SpringLayout.WEST, quickPlay, 0, SpringLayout.WEST, continuous);
            searchLayout.putConstraint(SpringLayout.NORTH, quickPlay, (int)(contLab.getPreferredSize().getHeight() + qpLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, contLab);
            searchLayout.putConstraint(SpringLayout.WEST, qpLab, (int)(quickPlay.getPreferredSize().getWidth() + 1), SpringLayout.WEST, quickPlay);
            searchLayout.putConstraint(SpringLayout.NORTH, qpLab, (int)(quickPlay.getPreferredSize().getHeight()/2 - qpLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, quickPlay);

            searchLayout.putConstraint(SpringLayout.WEST, equip, 0, SpringLayout.WEST, continuous);
            searchLayout.putConstraint(SpringLayout.NORTH, equip, (int)(qpLab.getPreferredSize().getHeight() + equipLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, qpLab);
            searchLayout.putConstraint(SpringLayout.WEST, equipLab, (int)(equip.getPreferredSize().getWidth() + 1), SpringLayout.WEST, equip);
            searchLayout.putConstraint(SpringLayout.NORTH, equipLab, (int)(equip.getPreferredSize().getHeight()/2 - equipLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, equip);

            searchLayout.putConstraint(SpringLayout.WEST, field, 0, SpringLayout.WEST, continuous);
            searchLayout.putConstraint(SpringLayout.NORTH, field, (int)(equipLab.getPreferredSize().getHeight() + fieldLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, equipLab);
            searchLayout.putConstraint(SpringLayout.WEST, fieldLab, (int)(field.getPreferredSize().getWidth() + 1), SpringLayout.WEST, field);
            searchLayout.putConstraint(SpringLayout.NORTH, fieldLab, (int)(field.getPreferredSize().getHeight()/2 - fieldLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, field);

            searchLayout.putConstraint(SpringLayout.WEST, counter, (int)(qpLab.getPreferredSize().getWidth()), SpringLayout.WEST, qpLab);
            searchLayout.putConstraint(SpringLayout.NORTH, counter, (int)(-quickPlay.getPreferredSize().getHeight()), SpringLayout.NORTH, quickPlay);
            searchLayout.putConstraint(SpringLayout.WEST, countLab, (int)(counter.getPreferredSize().getWidth() + 1), SpringLayout.WEST, counter);
            searchLayout.putConstraint(SpringLayout.NORTH, countLab, (int)(counter.getPreferredSize().getHeight()/2 - countLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, counter);
            
            searchLayout.putConstraint(SpringLayout.WEST, ritual, 0, SpringLayout.WEST, counter);
            searchLayout.putConstraint(SpringLayout.NORTH, ritual, (int)(countLab.getPreferredSize().getHeight() + ritLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, counter);
            searchLayout.putConstraint(SpringLayout.WEST, ritLab, (int)(ritual.getPreferredSize().getWidth() + 1), SpringLayout.WEST, ritual);
            searchLayout.putConstraint(SpringLayout.NORTH, ritLab, (int)(ritual.getPreferredSize().getHeight()/2 - ritLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, ritual);

            searchLayout.putConstraint(SpringLayout.WEST, normal, 0, SpringLayout.WEST, counter);
            searchLayout.putConstraint(SpringLayout.NORTH, normal, (int)(ritLab.getPreferredSize().getHeight() + normLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, ritual);
            searchLayout.putConstraint(SpringLayout.WEST, normLab, (int)(normal.getPreferredSize().getWidth() + 1), SpringLayout.WEST, normal);
            searchLayout.putConstraint(SpringLayout.NORTH, normLab, (int)(normal.getPreferredSize().getHeight()/2 - normLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, normal);

            /* ----- ATTACK SELECTION ----- */
            //Horizontal Separator from Link Rating Search
            JSeparator horizSepSix = new JSeparator(JSeparator.HORIZONTAL);
            horizSepSix.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            horizSepSix.setBackground(Color.BLACK);
            horizSepSix.setForeground(Color.RED);
            //Label for link rating section
            JLabel atkLabel = new JLabel("Attack:");
            atkLabel.setFont(mainFont.deriveFont(22f));
            atkLabel.setForeground(Color.WHITE);
            //LINK RATING SPINNER
            SpinnerModel atkLimit = new SpinnerNumberModel(-50, -50, 5000, 50);
            JSpinner atk = new JSpinner(atkLimit);
            atk.setFont(cardDetailsFont.deriveFont(22f));
            atk.setToolTipText("<html>Search by Attack (0-5000).<br>Select an attack of -50 to exclude from search.</html>");
            //Radio Button for less than
            JRadioButton atkLt = new JRadioButton("<", false);
            atkLt.setOpaque(false);
            atkLt.setFont(mainFont.deriveFont(22f));
            atkLt.setForeground(Color.WHITE);
            atkLt.setToolTipText("<html>Retrieves cards whose attack values are less than the chosen value.</html>");
            atkLt.setFocusPainted(false);
            //Radio Button for less than or equal to
            JRadioButton atkLte = new JRadioButton("<=", false);
            atkLte.setOpaque(false);
            atkLte.setFont(mainFont.deriveFont(22f));
            atkLte.setForeground(Color.WHITE);
            atkLte.setToolTipText("<html>Retrieves cards whose attack values are less than or equal to the chosen value.</html>");
            atkLte.setFocusPainted(false);
            //Radio Button for equal to
            JRadioButton atkEq = new JRadioButton("==", true);
            atkEq.setOpaque(false);
            atkEq.setFont(mainFont.deriveFont(22f));
            atkEq.setForeground(Color.WHITE);
            atkEq.setToolTipText("<html>Retrieves cards whose attack values are equal to the chosen value.</html>");
            atkEq.setFocusPainted(false);
            //Radio Button for greater than or equal to
            JRadioButton atkGte = new JRadioButton(">=", false);
            atkGte.setOpaque(false);
            atkGte.setFont(mainFont.deriveFont(22f));
            atkGte.setForeground(Color.WHITE);
            atkGte.setToolTipText("<html>Retrieves cards whose attack values are greater than or equal to the chosen value.</html>");
            atkGte.setFocusPainted(false);
            //Radio Button for greater than
            JRadioButton atkGt = new JRadioButton(">", false);
            atkGt.setOpaque(false);
            atkGt.setFont(mainFont.deriveFont(22f));
            atkGt.setForeground(Color.WHITE);
            atkGt.setToolTipText("<html>Retrieves cards whose attack values are greater than the chosen value.</html>");
            atkGt.setFocusPainted(false);
            //Adding
            srchPnl.add(atkLabel);
            srchPnl.add(horizSepSix);
            srchPnl.add(atk);
            srchPnl.add(atkLt);
            srchPnl.add(atkLte);
            srchPnl.add(atkEq);
            srchPnl.add(atkGte);
            srchPnl.add(atkGt);
            //UI Layout
            searchLayout.putConstraint(SpringLayout.WEST, horizSepSix, (int)(srchPnl.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, horizSepSix, (int)(monsTypeScroller.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, monsTypeScroller);

            searchLayout.putConstraint(SpringLayout.WEST, atkLabel, (int)(srchPnl.getPreferredSize().getWidth()/2 + 5), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, atkLabel, (int)(horizSepSix.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepSix);

            searchLayout.putConstraint(SpringLayout.WEST, atk, (int)(horizSepSix.getPreferredSize().getWidth()/2 + srchPnl.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, atk, (int)(atkLabel.getPreferredSize().getHeight()), SpringLayout.NORTH, atkLabel);

            searchLayout.putConstraint(SpringLayout.WEST, atkLt, (int)(horizSepSix.getPreferredSize().getWidth()/6), SpringLayout.WEST, horizSepSix);
            searchLayout.putConstraint(SpringLayout.NORTH, atkLt, (int)(atk.getPreferredSize().getHeight()), SpringLayout.NORTH, atk);

            searchLayout.putConstraint(SpringLayout.WEST, atkLte, (int)(horizSepSix.getPreferredSize().getWidth()/3), SpringLayout.WEST, horizSepSix);
            searchLayout.putConstraint(SpringLayout.NORTH, atkLte, (int)(atk.getPreferredSize().getHeight()), SpringLayout.NORTH, atk);

            searchLayout.putConstraint(SpringLayout.WEST, atkEq, (int)(horizSepSix.getPreferredSize().getWidth()/2), SpringLayout.WEST, horizSepSix);
            searchLayout.putConstraint(SpringLayout.NORTH, atkEq, (int)(atk.getPreferredSize().getHeight()), SpringLayout.NORTH, atk);

            searchLayout.putConstraint(SpringLayout.WEST, atkGte, (int)(horizSepSix.getPreferredSize().getWidth() * 2/3), SpringLayout.WEST, horizSepSix);
            searchLayout.putConstraint(SpringLayout.NORTH, atkGte, (int)(atk.getPreferredSize().getHeight()), SpringLayout.NORTH, atk);

            searchLayout.putConstraint(SpringLayout.WEST, atkGt, (int)(horizSepSix.getPreferredSize().getWidth() * 5/6), SpringLayout.WEST, horizSepSix);
            searchLayout.putConstraint(SpringLayout.NORTH, atkGt, (int)(atk.getPreferredSize().getHeight()), SpringLayout.NORTH, atk);
            //Make linkRateComparison button group
            ButtonGroup atkComparisonButtonGroup = new ButtonGroup();
            atkComparisonButtonGroup.add(atkLt);
            atkComparisonButtonGroup.add(atkLte);
            atkComparisonButtonGroup.add(atkEq);
            atkComparisonButtonGroup.add(atkGte);
            atkComparisonButtonGroup.add(atkGt);

            /* ----- ATTACK SELECTION ----- */
            //Horizontal Separator from Link Rating Search
            JSeparator horizSepSeven = new JSeparator(JSeparator.HORIZONTAL);
            horizSepSeven.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            horizSepSeven.setBackground(Color.BLACK);
            horizSepSeven.setForeground(Color.RED);
            //Label for link rating section
            JLabel defLabel = new JLabel("Defense:");
            defLabel.setFont(mainFont.deriveFont(22f));
            defLabel.setForeground(Color.WHITE);
            //LINK RATING SPINNER
            SpinnerModel defLimit = new SpinnerNumberModel(-50, -50, 5000, 50);
            JSpinner def = new JSpinner(defLimit);
            def.setFont(cardDetailsFont.deriveFont(22f));
            def.setToolTipText("<html>Search by Defense (0-5000).<br>Select a defense of -50 to exclude from search.</html>");
            //Radio Button for less than
            JRadioButton defLt = new JRadioButton("<", false);
            defLt.setOpaque(false);
            defLt.setFont(mainFont.deriveFont(22f));
            defLt.setForeground(Color.WHITE);
            defLt.setToolTipText("<html>Retrieves cards whose defense values are less than the chosen value.</html>");
            defLt.setFocusPainted(false);
            //Radio Button for less than or equal to
            JRadioButton defLte = new JRadioButton("<=", false);
            defLte.setOpaque(false);
            defLte.setFont(mainFont.deriveFont(22f));
            defLte.setForeground(Color.WHITE);
            defLte.setToolTipText("<html>Retrieves cards whose defense values are less than or equal to the chosen value.</html>");
            defLte.setFocusPainted(false);
            //Radio Button for equal to
            JRadioButton defEq = new JRadioButton("==", true);
            defEq.setOpaque(false);
            defEq.setFont(mainFont.deriveFont(22f));
            defEq.setForeground(Color.WHITE);
            defEq.setToolTipText("<html>Retrieves cards whose defense values are equal to the chosen value.</html>");
            defEq.setFocusPainted(false);
            //Radio Button for greater than or equal to
            JRadioButton defGte = new JRadioButton(">=", false);
            defGte.setOpaque(false);
            defGte.setFont(mainFont.deriveFont(22f));
            defGte.setForeground(Color.WHITE);
            defGte.setToolTipText("<html>Retrieves cards whose defense values are greater than or equal to the chosen value.</html>");
            defGte.setFocusPainted(false);
            //Radio Button for greater than
            JRadioButton defGt = new JRadioButton(">", false);
            defGt.setOpaque(false);
            defGt.setFont(mainFont.deriveFont(22f));
            defGt.setForeground(Color.WHITE);
            defGt.setToolTipText("<html>Retrieves cards whose defense values are greater than the chosen value.</html>");
            defGt.setFocusPainted(false);
            //Adding
            srchPnl.add(defLabel);
            srchPnl.add(horizSepSeven);
            srchPnl.add(def);
            srchPnl.add(defLt);
            srchPnl.add(defLte);
            srchPnl.add(defEq);
            srchPnl.add(defGte);
            srchPnl.add(defGt);
            //UI Layout
            searchLayout.putConstraint(SpringLayout.WEST, horizSepSeven, (int)(srchPnl.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, horizSepSeven, (int)(atkEq.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, atkEq);

            searchLayout.putConstraint(SpringLayout.WEST, defLabel, (int)(srchPnl.getPreferredSize().getWidth()/2 + 5), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, defLabel, (int)(horizSepSeven.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepSeven);

            searchLayout.putConstraint(SpringLayout.WEST, def, (int)(horizSepSeven.getPreferredSize().getWidth()/2 + srchPnl.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, def, (int)(defLabel.getPreferredSize().getHeight()), SpringLayout.NORTH, defLabel);

            searchLayout.putConstraint(SpringLayout.WEST, defLt, (int)(horizSepSeven.getPreferredSize().getWidth()/6), SpringLayout.WEST, horizSepSeven);
            searchLayout.putConstraint(SpringLayout.NORTH, defLt, (int)(def.getPreferredSize().getHeight()), SpringLayout.NORTH, def);

            searchLayout.putConstraint(SpringLayout.WEST, defLte, (int)(horizSepSeven.getPreferredSize().getWidth()/3), SpringLayout.WEST, horizSepSeven);
            searchLayout.putConstraint(SpringLayout.NORTH, defLte, (int)(def.getPreferredSize().getHeight()), SpringLayout.NORTH, def);

            searchLayout.putConstraint(SpringLayout.WEST, defEq, (int)(horizSepSeven.getPreferredSize().getWidth()/2), SpringLayout.WEST, horizSepSeven);
            searchLayout.putConstraint(SpringLayout.NORTH, defEq, (int)(def.getPreferredSize().getHeight()), SpringLayout.NORTH, def);

            searchLayout.putConstraint(SpringLayout.WEST, defGte, (int)(horizSepSeven.getPreferredSize().getWidth() * 2/3), SpringLayout.WEST, horizSepSeven);
            searchLayout.putConstraint(SpringLayout.NORTH, defGte, (int)(def.getPreferredSize().getHeight()), SpringLayout.NORTH, def);

            searchLayout.putConstraint(SpringLayout.WEST, defGt, (int)(horizSepSeven.getPreferredSize().getWidth() * 5/6), SpringLayout.WEST, horizSepSeven);
            searchLayout.putConstraint(SpringLayout.NORTH, defGt, (int)(def.getPreferredSize().getHeight()), SpringLayout.NORTH, def);
            //Make linkRateComparison button group
            ButtonGroup defComparisonButtonGroup = new ButtonGroup();
            defComparisonButtonGroup.add(defLt);
            defComparisonButtonGroup.add(defLte);
            defComparisonButtonGroup.add(defEq);
            defComparisonButtonGroup.add(defGte);
            defComparisonButtonGroup.add(defGt);

            /* ----- FINAL SEPARATOR ----- */
            JSeparator finalHorizSep = new JSeparator(JSeparator.HORIZONTAL);
            finalHorizSep.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()), 5));
            finalHorizSep.setBackground(Color.BLACK);
            finalHorizSep.setForeground(Color.RED);

            /* ----- EXIT BUTTON ----- */
            //Create a button that allows the user to close the program
            JButton srchExitButton = new JButton("Exit", new ImageIcon("img/system/tabExitIcon.png"));
            //Make the button transparent
            srchExitButton.setOpaque(false);
            srchExitButton.setContentAreaFilled(false);
            srchExitButton.setFocusPainted(false);
            srchExitButton.setBorderPainted(false);
            //Ensure that text and image are drawn over each other
            srchExitButton.setVerticalTextPosition(JButton.BOTTOM);
            srchExitButton.setHorizontalTextPosition(JButton.CENTER);
            //Set color and size of font
            srchExitButton.setForeground(Color.RED);
            srchExitButton.setFont(mainFont.deriveFont(16f));
            //Set tooltip
            srchExitButton.setToolTipText("Close the database.");
            
            /* ----- SEARCH BUTTON ----- */
            JButton search = new JButton("Search", new ImageIcon("img/system/srchIcon.png"));
            search.setFont(mainFont.deriveFont(22f));
            Dimension sizes = search.getPreferredSize();
            sizes.width = (int)(srchPnl.getPreferredSize().getWidth() - (40 + srchExitButton.getPreferredSize().getWidth()));
            search.setPreferredSize(sizes);
            search.setBackground(new Color(88, 17, 17));
            search.setForeground(new Color(218, 165, 32));
            search.setFocusPainted(false);
            search.setToolTipText("Search Yu-Gi-Oh! cards...");
            
            srchPnl.add(search);
            searchLayout.putConstraint(SpringLayout.WEST, search, (int)(32 + srchExitButton.getPreferredSize().getWidth()), SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, search, (int)(finalHorizSep.getPreferredSize().getHeight()), SpringLayout.NORTH, finalHorizSep);

            srchPnl.add(srchExitButton);
            searchLayout.putConstraint(SpringLayout.WEST, srchExitButton, 27, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, srchExitButton, (int)(finalHorizSep.getPreferredSize().getHeight()), SpringLayout.NORTH, finalHorizSep);

            srchPnl.add(finalHorizSep);
            searchLayout.putConstraint(SpringLayout.WEST, finalHorizSep, 0, SpringLayout.WEST, srchPnl);
            searchLayout.putConstraint(SpringLayout.NORTH, finalHorizSep, (int)(defEq.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, defEq);
        } catch (IOException e) {
            // TODO: handle exception
        }

        /* RESULTS PANEL */
        try {
            resultsImg = ImageIO.read(new File("img/system/resultsBg.jpg"));
            resPnl = new ImagePanel(resultsImg);
            resultsLayout = new SpringLayout();
            resPnl.setLayout(resultsLayout);

            dbTabPane.addTab("Results", resPnl);
            dbTabPane.setIconAt(1, new ImageIcon("img/system/resIcon.png"));

            //Work with results panel
            /* ----- EXIT BUTTON ----- */
            //Create a button that allows the user to close the program
            JButton resExitButton = new JButton("Exit", new ImageIcon("img/system/tabExitIcon.png"));
            //Make the button transparent
            resExitButton.setOpaque(false);
            resExitButton.setContentAreaFilled(false);
            resExitButton.setFocusPainted(false);
            resExitButton.setBorderPainted(false);
            //Ensure that text and image are drawn over each other
            resExitButton.setVerticalTextPosition(JButton.BOTTOM);
            resExitButton.setHorizontalTextPosition(JButton.CENTER);
            //Set color and size of font
            resExitButton.setForeground(Color.CYAN);
            resExitButton.setFont(mainFont.deriveFont(16f));
            //Set tooltip
            resExitButton.setToolTipText("Close the database.");
            //Adding and UI layout
            resPnl.add(resExitButton);
            resultsLayout.putConstraint(SpringLayout.WEST, resExitButton, (int)(resPnl.getPreferredSize().getWidth() - (resExitButton.getPreferredSize().getWidth() + 27)), SpringLayout.WEST, resPnl);
            resultsLayout.putConstraint(SpringLayout.NORTH, resExitButton, 5, SpringLayout.NORTH, resPnl);

            /* ----- CARD SELECTION COMBO BOX AND BUTTON ----- */
            //Label for combo box
            JLabel resLabel = new JLabel("Results:");
            resLabel.setFont(mainFont.deriveFont(22f));
            resLabel.setForeground(Color.WHITE);
            //FilterComboBox for card selection
            cardRes = new ArrayList<String>();
            cardRes.add("");
            cardRes.add("Dark Magician");
            cardRes.add("Dark Magician Girl");
            cardRes.add("Dark Magic Attack");
            cardRes.add("Revealer of the Ice Barrier");
            cardRes.add("Medallion of the Ice Barrier");
            cardRes.add("Ice Barrier");
            cardRes.add("D/D/D/D Super-Dimensional Sovereign Emperor Zero Paradox");
            FilterComboBox cardComboBox = new FilterComboBox(cardRes);
            cardComboBox.setEditable(true);
            cardComboBox.setSelectedItem("");
            cardComboBox.setFont(cardNameFont.deriveFont(22f));
            cardComboBox.setToolTipText("Select a card to display.");
            //JButton to display results
            JButton dispCard = new JButton("Display");
            dispCard.setFont(mainFont.deriveFont(22f));
            dispCard.setBackground(new Color(47, 66, 114));
            dispCard.setForeground(new Color(218, 165, 32));
            dispCard.setFocusPainted(false);
            dispCard.setToolTipText("Display the selected card.");
            //UI Constraints
            resultsLayout.putConstraint(SpringLayout.WEST, resLabel, 5, SpringLayout.WEST, resPnl);
            resultsLayout.putConstraint(SpringLayout.NORTH, resLabel, (int)(resExitButton.getPreferredSize().getHeight()/2 - resLabel.getPreferredSize().getHeight()/2), SpringLayout.NORTH, resPnl);
            resultsLayout.putConstraint(SpringLayout.WEST, cardComboBox, (int)(resLabel.getPreferredSize().getWidth() + 1), SpringLayout.WEST, resLabel);
            resultsLayout.putConstraint(SpringLayout.NORTH, cardComboBox, 0, SpringLayout.NORTH, resLabel);
            resultsLayout.putConstraint(SpringLayout.WEST, dispCard, (int)(resLabel.getPreferredSize().getWidth() + cardComboBox.getPreferredSize().getWidth()), SpringLayout.WEST, cardComboBox);
            resultsLayout.putConstraint(SpringLayout.NORTH, dispCard, 0, SpringLayout.NORTH, resLabel);
            //Adding
            resPnl.add(resLabel);
            resPnl.add(cardComboBox);
            resPnl.add(dispCard);

            /* ----- MAIN UI ----- */
            //Horizontal Separator from Name Search
            JSeparator horizSepTop = new JSeparator(JSeparator.HORIZONTAL);
            horizSepTop.setPreferredSize(new Dimension((int)(resPnl.getPreferredSize().getWidth()), 5));
            horizSepTop.setBackground(Color.BLACK);
            horizSepTop.setForeground(Color.CYAN);
            //Adding and UI
            resPnl.add(horizSepTop);
            resultsLayout.putConstraint(SpringLayout.WEST, horizSepTop, 0, SpringLayout.WEST, resPnl);
            resultsLayout.putConstraint(SpringLayout.NORTH, horizSepTop, (int)(resExitButton.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, resExitButton);
            //Normal Monster Panel
            /* TEST ATTRIBUTES -- DELETE LATER */
                String testId = "123709";
                String monName = "Laval Lancelord";
                String[] typeline = {"Warrior", "Effect"};
                String frameType = "effect";
                String desc = "You can Normal Summon this card without Tributing. If you do, during the End Phase: Send it to the Graveyard. When this card on the field is destroyed and sent to the Graveyard: You can target 1 of your banished FIRE monsters; add that target to your hand.";
                int atk = 2100;
                int def = 200;
                int level = 6;
                String attribute = "FIRE";
            JPanel effectMons = new JPanel();
            effectMons.setBackground(new Color(0,0,0,0));
            effectMons.setPreferredSize(new Dimension((int)(resPnl.getPreferredSize().getWidth()), (int)(resPnl.getPreferredSize().getHeight() * 8.5/10)));
            SpringLayout effectMonsLayout = new SpringLayout();
            effectMons.setLayout(effectMonsLayout);
            
            //Monster Name Label
            JLabel effectMonName = new JLabel(monName);
            effectMonName.setFont(cardNameFont.deriveFont(36f));
            effectMonName.setForeground(Color.WHITE);
            effectMonName.setBackground(new Color(47, 66, 114, 192));
            effectMonName.setOpaque(true);
            effectMonName.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.CYAN, Color.BLACK));
            Dimension sizesTwo = effectMonName.getPreferredSize();
            sizesTwo.width = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()) - 10;
            effectMonName.setPreferredSize(sizesTwo);
            effectMonName.setHorizontalAlignment(JLabel.CENTER);
            //Monster Levels
            JLabel monsLevelStar = new JLabel(new ImageIcon("img/cardInfo/monsLvl.png"));
            monsLevelStar.setFont(cardDetailsFont.deriveFont(30f));
            monsLevelStar.setForeground(Color.WHITE);
            monsLevelStar.setBackground(new Color(47, 66, 114, 192));
            monsLevelStar.setOpaque(true);
            monsLevelStar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.CYAN, Color.BLACK));
            sizesTwo = monsLevelStar.getPreferredSize();
            sizesTwo.width = ((int)(effectMonName.getPreferredSize().getWidth()) - 337)/2;
            monsLevelStar.setPreferredSize(sizesTwo);
            monsLevelStar.setHorizontalAlignment(JLabel.LEFT);
            monsLevelStar.setText(String.format("Level %" + (int)(monsLevelStar.getPreferredSize().getWidth() - 473) + "d", level));
            //Monster Attribute
            JLabel effectMonAttr = new JLabel(new ImageIcon("img/attributes/" + attribute.toLowerCase() + ".png"));
            effectMonAttr.setFont(cardSetFont.deriveFont(30f));
            effectMonAttr.setText(attribute);
            effectMonAttr.setForeground(Color.WHITE);
            effectMonAttr.setBackground(new Color(47, 66, 114, 192));
            effectMonAttr.setOpaque(true);
            effectMonAttr.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.CYAN, Color.BLACK));
            sizesTwo = effectMonAttr.getPreferredSize();
            sizesTwo.width = ((int)(effectMonName.getPreferredSize().getWidth()) - 327)/2;
            effectMonAttr.setPreferredSize(sizesTwo);
            effectMonAttr.setHorizontalAlignment(JLabel.LEFT);
            //Monster Type Line
            JLabel effectMonTypes = new JLabel();
            String finalTypeLine = "";
            for(int t = 0;t < typeline.length;t++) {
                finalTypeLine += typeline[t];
                if(t != typeline.length - 1) {
                    finalTypeLine += "/";
                }
            }
            effectMonTypes.setText(finalTypeLine);
            effectMonTypes.setFont(cardDetailsFont.deriveFont(30f));
            effectMonTypes.setForeground(Color.WHITE);
            effectMonTypes.setBackground(new Color(47, 66, 114, 192));
            effectMonTypes.setOpaque(true);
            effectMonTypes.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.CYAN, Color.BLACK));
            effectMonTypes.setHorizontalAlignment(JLabel.CENTER);
            sizesTwo = effectMonTypes.getPreferredSize();
            sizesTwo.width = (int)(effectMonAttr.getPreferredSize().getWidth() + 15 + monsLevelStar.getPreferredSize().getWidth());
            effectMonTypes.setPreferredSize(sizesTwo);
            //Card Attack
            JLabel effectMonAtk = new JLabel();
            effectMonAtk.setFont(cardDetailsFont.deriveFont(30f));
            effectMonAtk.setText(attribute);
            effectMonAtk.setForeground(Color.WHITE);
            effectMonAtk.setBackground(new Color(47, 66, 114, 192));
            effectMonAtk.setOpaque(true);
            effectMonAtk.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.CYAN, Color.BLACK));
            sizesTwo = effectMonAtk.getPreferredSize();
            sizesTwo.width = ((int)(effectMonName.getPreferredSize().getWidth()) - 327)/2;
            effectMonAtk.setPreferredSize(sizesTwo);
            effectMonAtk.setHorizontalAlignment(JLabel.LEFT);
            effectMonAtk.setText(String.format("ATK %" + (int)(effectMonAtk.getPreferredSize().getWidth() - 471) + "d", atk));
            //Card Defense
            JLabel effectMonDef = new JLabel();
            effectMonDef.setFont(cardDetailsFont.deriveFont(30f));
            effectMonDef.setText(attribute);
            effectMonDef.setForeground(Color.WHITE);
            effectMonDef.setBackground(new Color(47, 66, 114, 192));
            effectMonDef.setOpaque(true);
            effectMonDef.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.CYAN, Color.BLACK));
            sizesTwo = effectMonDef.getPreferredSize();
            sizesTwo.width = ((int)(effectMonName.getPreferredSize().getWidth()) - 337)/2;
            effectMonDef.setPreferredSize(sizesTwo);
            effectMonDef.setHorizontalAlignment(JLabel.LEFT);
            effectMonDef.setText(String.format("DEF %" + (int)(effectMonDef.getPreferredSize().getWidth() - 466) + "d", def));
            
            //Card Art
            JLabel effectMonArt = new JLabel();

            effectMonArt.setPreferredSize(new Dimension(312, 312));
            art = ImageIO.read(new File("img/cardArt/" + testId + ".jpg"));
            scaledArt = art.getScaledInstance((int)(effectMonArt.getPreferredSize().getWidth()), (int)(effectMonArt.getPreferredSize().getHeight()), Image.SCALE_SMOOTH);
            finalArt = new ImageIcon(scaledArt);
            effectMonArt.setIcon(finalArt);
            effectMonArt.addMouseListener(new MouseListener() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JWindow w = new JWindow();
                    w.setBackground(new Color(0, 0, 0, 0));
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception f) {
                    }
                    
                    JPanel windowPanel;
                    JLabel cardName = new JLabel(monName);
                    cardName.setFont(cardNameFont.deriveFont(30f));
                    cardName.setForeground(Color.WHITE);
                    JLabel zoomedArt = new JLabel(new ImageIcon(art));
                    JButton windowCloser = new JButton("Close");
                    windowCloser.setFont(mainFont.deriveFont(22f));
                    windowCloser.setForeground(Color.BLACK);
                    windowCloser.setFocusPainted(false);
                    
                    windowPanel = new JPanel() {
                        public void paintComponent(Graphics g) {
                            g.setColor(new Color(47,66,114));
                            g.fillRoundRect(0, 0, 630, 710, 20, 20);

                            // g.setColor(new Color(10,10,255));
                            g.drawRoundRect(0, 0, 630, 710, 20, 20);

                            //Create glossy appearance
                            for(int i = 0;i < 710;i++) {
                                g.setColor(new Color(255,255,255,(int)(i/Math.log(710))));
                                g.drawLine(0, i, 630, i);
                            }
                        }
                    };

                    windowCloser.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            w.setVisible(false);
                        };
                    });
                    
                    windowPanel.add(cardName);
                    windowPanel.add(zoomedArt);
                    windowPanel.add(windowCloser);
                    w.add(windowPanel);

                    w.setSize(630, 710);
                    w.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - w.getWidth()/2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - w.getHeight()/2));
                    w.setVisible(true);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    ;
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    ;
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ;
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    ;
                }
            });

            //Adding to effectMons
            effectMons.add(effectMonName);
            effectMons.add(monsLevelStar);
            effectMons.add(effectMonArt);
            effectMons.add(effectMonAttr);
            effectMons.add(effectMonTypes);
            effectMons.add(effectMonAtk);
            effectMons.add(effectMonDef);
            //effectMons Layout
            // int levelPadWest = (int)(-(levelTwo.getPreferredSize().getWidth() + 5));
            // int levelPadNorth = (int)(effectMonName.getPreferredSize().getHeight() + 1);

            effectMonsLayout.putConstraint(SpringLayout.WEST, effectMonName, 5, SpringLayout.WEST, effectMons);
            effectMonsLayout.putConstraint(SpringLayout.NORTH, effectMonName, 5, SpringLayout.NORTH, effectMons);

            effectMonsLayout.putConstraint(SpringLayout.WEST, effectMonArt, 5, SpringLayout.WEST, effectMons);
            effectMonsLayout.putConstraint(SpringLayout.NORTH, effectMonArt, (int)(effectMonName.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, effectMonName);

            effectMonsLayout.putConstraint(SpringLayout.WEST, effectMonAttr, (int)(effectMonArt.getPreferredSize().getWidth() + 5), SpringLayout.WEST, effectMonArt);
            effectMonsLayout.putConstraint(SpringLayout.NORTH, effectMonAttr, 0, SpringLayout.NORTH, effectMonArt);

            effectMonsLayout.putConstraint(SpringLayout.WEST, monsLevelStar, (int)(effectMonAttr.getPreferredSize().getWidth() + 15), SpringLayout.WEST, effectMonAttr);
            effectMonsLayout.putConstraint(SpringLayout.NORTH, monsLevelStar, 0, SpringLayout.NORTH, effectMonArt);

            effectMonsLayout.putConstraint(SpringLayout.WEST, effectMonTypes, 0, SpringLayout.WEST, effectMonAttr);
            effectMonsLayout.putConstraint(SpringLayout.NORTH, effectMonTypes, (int)(effectMonAttr.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, effectMonAttr);

            effectMonsLayout.putConstraint(SpringLayout.WEST, effectMonAtk, 0, SpringLayout.WEST, effectMonAttr);
            effectMonsLayout.putConstraint(SpringLayout.NORTH, effectMonAtk, (int)(effectMonTypes.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, effectMonTypes);

            effectMonsLayout.putConstraint(SpringLayout.WEST, effectMonDef, (int)(effectMonAtk.getPreferredSize().getWidth() + 15), SpringLayout.WEST, effectMonAtk);
            effectMonsLayout.putConstraint(SpringLayout.NORTH, effectMonDef, 0, SpringLayout.NORTH, effectMonAtk);

            //Adding and UI
            resPnl.add(effectMons);
            resultsLayout.putConstraint(SpringLayout.WEST, effectMons, 0, SpringLayout.WEST, resPnl);
            resultsLayout.putConstraint(SpringLayout.NORTH, effectMons, (int)(horizSepTop.getPreferredSize().getHeight() + 1), SpringLayout.NORTH, horizSepTop);

            // //Label attribute section
            // JLabel attrLabel = new JLabel("Attribute:");
            // attrLabel.setFont(mainFont.deriveFont(22f));
            // attrLabel.setForeground(Color.WHITE);
            // //Checkboxes per attribute
            // //DARK CHECKBOX
            // JCheckBox dark = new JCheckBox();
            // dark.setOpaque(false);
            // JLabel darkLab = new JLabel("Dark", new ImageIcon("img/attributes/darkOpt.png"), JLabel.LEFT);
            // darkLab.setFont(cardSetFont.deriveFont(22f));
            // darkLab.setForeground(Color.WHITE);
            // dark.setMnemonic(KeyEvent.VK_D);
            // dark.setSelected(false);
            // dark.setToolTipText("<html>Select to search for the <i>DARK</i> attribute.<br>Shortcut: Alt+D.</html>");
            // //DIVINE CHECKBOX
            // JCheckBox divine = new JCheckBox();
            // divine.setOpaque(false);
            // JLabel divineLab = new JLabel("Divine", new ImageIcon("img/attributes/divineOpt.png"), JLabel.LEFT);
            // divineLab.setFont(cardSetFont.deriveFont(22f));
            // divineLab.setForeground(Color.WHITE);
            // divine.setMnemonic(KeyEvent.VK_I);
            // divine.setSelected(false);
            // divine.setToolTipText("<html>Select to search for the <i>DIVINE</i> attribute.<br>Shortcut: Alt+I.</html>");
            // //EARTH CHECKBOX
            // JCheckBox earth = new JCheckBox();
            // earth.setOpaque(false);
            // JLabel earthLab = new JLabel("Earth", new ImageIcon("img/attributes/earthOpt.png"), JLabel.LEFT);
            // earthLab.setFont(cardSetFont.deriveFont(22f));
            // earthLab.setForeground(Color.WHITE);
            // earth.setMnemonic(KeyEvent.VK_E);
            // earth.setSelected(false);
            // earth.setToolTipText("<html>Select to search for the <i>EARTH</i> attribute.<br>Shortcut: Alt+E.</html>");
            // //FIRE CHECKBOX
            // JCheckBox fire = new JCheckBox();
            // fire.setOpaque(false);
            // JLabel fireLab = new JLabel("Fire", new ImageIcon("img/attributes/fireOpt.png"), JLabel.LEFT);
            // fireLab.setFont(cardSetFont.deriveFont(22f));
            // fireLab.setForeground(Color.WHITE);
            // fire.setMnemonic(KeyEvent.VK_F);
            // fire.setSelected(false);
            // fire.setToolTipText("<html>Select to search for the <i>FIRE</i> attribute.<br>Shortcut: Alt+F.</html>");
            // //LIGHT CHECKBOX
            // JCheckBox light = new JCheckBox();
            // light.setOpaque(false);
            // JLabel lightLab = new JLabel("Light", new ImageIcon("img/attributes/lightOpt.png"), JLabel.LEFT);
            // lightLab.setFont(cardSetFont.deriveFont(22f));
            // lightLab.setForeground(Color.WHITE);
            // light.setMnemonic(KeyEvent.VK_L);
            // light.setSelected(false);
            // light.setToolTipText("<html>Select to search for the <i>LIGHT</i> attribute.<br>Shortcut: Alt+L.</html>");
            // //SPELL CHECKBOX
            // JCheckBox spell = new JCheckBox();
            // spell.setOpaque(false);
            // JLabel spellLab = new JLabel("Spell", new ImageIcon("img/attributes/spellOpt.png"), JLabel.LEFT);
            // spellLab.setFont(cardSetFont.deriveFont(22f));
            // spellLab.setForeground(Color.WHITE);
            // spell.setMnemonic(KeyEvent.VK_S);
            // spell.setSelected(false);
            // spell.setToolTipText("<html>Select to search for the <i>SPELL</i> attribute.<br>Shortcut: Alt+S.</html>");
            // //TRAP CHECKBOX
            // JCheckBox trap = new JCheckBox();
            // trap.setOpaque(false);
            // JLabel trapLab = new JLabel("Trap", new ImageIcon("img/attributes/trapOpt.png"), JLabel.LEFT);
            // trapLab.setFont(cardSetFont.deriveFont(22f));
            // trapLab.setForeground(Color.WHITE);
            // trap.setMnemonic(KeyEvent.VK_T);
            // trap.setSelected(false);
            // trap.setToolTipText("<html>Select to search for the <i>TRAP</i> attribute.<br>Shortcut: Alt+T.</html>");
            // //WATER CHECKBOX
            // JCheckBox water = new JCheckBox();
            // water.setOpaque(false);
            // JLabel waterLab = new JLabel("Water", new ImageIcon("img/attributes/waterOpt.png"), JLabel.LEFT);
            // waterLab.setFont(cardSetFont.deriveFont(22f));
            // waterLab.setForeground(Color.WHITE);
            // water.setMnemonic(KeyEvent.VK_W);
            // water.setSelected(false);
            // water.setToolTipText("<html>Select to search for the <i>WATER</i> attribute.<br>Shortcut: Alt+W.</html>");
            // //WIND CHECKBOX
            // JCheckBox wind = new JCheckBox();
            // wind.setOpaque(false);
            // JLabel windLab = new JLabel("Wind", new ImageIcon("img/attributes/windOpt.png"), JLabel.LEFT);
            // windLab.setFont(cardSetFont.deriveFont(22f));
            // windLab.setForeground(Color.WHITE);
            // wind.setMnemonic(KeyEvent.VK_N);
            // wind.setSelected(false);
            // wind.setToolTipText("<html>Select to search for the <i>WIND</i> attribute.<br>Shortcut: Alt+N.</html>");
            // //Adding
            // srchPnl.add(attrLabel);
            // srchPnl.add(dark);
            // srchPnl.add(darkLab);
            // srchPnl.add(horizSepOne);
            // srchPnl.add(divine);
            // srchPnl.add(divineLab);
            // srchPnl.add(earth);
            // srchPnl.add(earthLab);
            // srchPnl.add(fire);
            // srchPnl.add(fireLab);
            // srchPnl.add(light);
            // srchPnl.add(lightLab);
            // srchPnl.add(spell);
            // srchPnl.add(spellLab);
            // srchPnl.add(trap);
            // srchPnl.add(trapLab);
            // srchPnl.add(water);
            // srchPnl.add(waterLab);
            // srchPnl.add(wind);
            // srchPnl.add(windLab);
            // //UI Layout
            // searchLayout.putConstraint(SpringLayout.WEST, attrLabel, 5, SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, attrLabel, (int)(horizSepOne.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepOne);

            // searchLayout.putConstraint(SpringLayout.WEST, dark, 25, SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, dark, (int)(attrLabel.getPreferredSize().getHeight() + darkLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, attrLabel);
            // searchLayout.putConstraint(SpringLayout.WEST, darkLab, (int)(dark.getPreferredSize().getWidth() + 1), SpringLayout.WEST, dark);
            // searchLayout.putConstraint(SpringLayout.NORTH, darkLab, (int)(dark.getPreferredSize().getHeight()/2 - darkLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, dark);

            // searchLayout.putConstraint(SpringLayout.WEST, divine, (int)(darkLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, darkLab);
            // searchLayout.putConstraint(SpringLayout.NORTH, divine, (int)(attrLabel.getPreferredSize().getHeight() + divineLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, attrLabel);
            // searchLayout.putConstraint(SpringLayout.WEST, divineLab, (int)(divine.getPreferredSize().getWidth() + 1), SpringLayout.WEST, divine);
            // searchLayout.putConstraint(SpringLayout.NORTH, divineLab, (int)(divine.getPreferredSize().getHeight()/2 - divineLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, divine);

            // searchLayout.putConstraint(SpringLayout.WEST, earth, (int)(divineLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, divineLab);
            // searchLayout.putConstraint(SpringLayout.NORTH, earth, (int)(attrLabel.getPreferredSize().getHeight() + earthLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, attrLabel);
            // searchLayout.putConstraint(SpringLayout.WEST, earthLab, (int)(earth.getPreferredSize().getWidth() + 1), SpringLayout.WEST, earth);
            // searchLayout.putConstraint(SpringLayout.NORTH, earthLab, (int)(earth.getPreferredSize().getHeight()/2 - earthLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, earth);

            // searchLayout.putConstraint(SpringLayout.WEST, fire, 0, SpringLayout.WEST, dark);
            // searchLayout.putConstraint(SpringLayout.NORTH, fire, (int)(darkLab.getPreferredSize().getHeight() + fireLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, dark);
            // searchLayout.putConstraint(SpringLayout.WEST, fireLab, (int)(fire.getPreferredSize().getWidth() + 1), SpringLayout.WEST, fire);
            // searchLayout.putConstraint(SpringLayout.NORTH, fireLab, (int)(fire.getPreferredSize().getHeight()/2 - fireLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, fire);

            // searchLayout.putConstraint(SpringLayout.WEST, light, (int)(darkLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, fireLab);
            // searchLayout.putConstraint(SpringLayout.NORTH, light, (int)(divineLab.getPreferredSize().getHeight() + lightLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, divine);
            // searchLayout.putConstraint(SpringLayout.WEST, lightLab, (int)(light.getPreferredSize().getWidth() + 1), SpringLayout.WEST, light);
            // searchLayout.putConstraint(SpringLayout.NORTH, lightLab, (int)(light.getPreferredSize().getHeight()/2 - lightLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, light);
            
            // searchLayout.putConstraint(SpringLayout.WEST, spell, (int)(divineLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, lightLab);
            // searchLayout.putConstraint(SpringLayout.NORTH, spell, (int)(earthLab.getPreferredSize().getHeight() + spellLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, earth);
            // searchLayout.putConstraint(SpringLayout.WEST, spellLab, (int)(spell.getPreferredSize().getWidth() + 1), SpringLayout.WEST, spell);
            // searchLayout.putConstraint(SpringLayout.NORTH, spellLab, (int)(spell.getPreferredSize().getHeight()/2 - spellLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, spell);

            // searchLayout.putConstraint(SpringLayout.WEST, trap, 0, SpringLayout.WEST, fire);
            // searchLayout.putConstraint(SpringLayout.NORTH, trap, (int)(fireLab.getPreferredSize().getHeight() + trapLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, fire);
            // searchLayout.putConstraint(SpringLayout.WEST, trapLab, (int)(trap.getPreferredSize().getWidth() + 1), SpringLayout.WEST, trap);
            // searchLayout.putConstraint(SpringLayout.NORTH, trapLab, (int)(trap.getPreferredSize().getHeight()/2 - trapLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, trap);

            // searchLayout.putConstraint(SpringLayout.WEST, water, (int)(darkLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, trapLab);
            // searchLayout.putConstraint(SpringLayout.NORTH, water, (int)(lightLab.getPreferredSize().getHeight() + waterLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, light);
            // searchLayout.putConstraint(SpringLayout.WEST, waterLab, (int)(water.getPreferredSize().getWidth() + 1), SpringLayout.WEST, water);
            // searchLayout.putConstraint(SpringLayout.NORTH, waterLab, (int)(water.getPreferredSize().getHeight()/2 - waterLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, water);

            // searchLayout.putConstraint(SpringLayout.WEST, wind, (int)(divineLab.getPreferredSize().getWidth() + 25), SpringLayout.WEST, waterLab);
            // searchLayout.putConstraint(SpringLayout.NORTH, wind, (int)(spellLab.getPreferredSize().getHeight() + windLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, spell);
            // searchLayout.putConstraint(SpringLayout.WEST, windLab, (int)(wind.getPreferredSize().getWidth() + 1), SpringLayout.WEST, wind);
            // searchLayout.putConstraint(SpringLayout.NORTH, windLab, (int)(wind.getPreferredSize().getHeight()/2 - windLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, wind);
        
            // /* ----- LEVEL/RANK SELECTION ----- */
            // //Horizontal Separator from Attribute Search
            // JSeparator horizSepTwo = new JSeparator(JSeparator.HORIZONTAL);
            // horizSepTwo.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            // horizSepTwo.setBackground(Color.BLACK);
            // horizSepTwo.setForeground(Color.RED);
            // //Label for level section
            // JLabel lvlRnkLabel = new JLabel("Level/Rank:");
            // lvlRnkLabel.setFont(mainFont.deriveFont(22f));
            // lvlRnkLabel.setForeground(Color.WHITE);
            // //LEVEL/RANK SPINNER
            // SpinnerModel levelLimit = new SpinnerNumberModel(-1, -1, 13, 1);
            // JSpinner lvlRnk = new JSpinner(levelLimit);
            // lvlRnk.setFont(cardDetailsFont.deriveFont(22f));
            // lvlRnk.setToolTipText("<html>Search by Level/Rank (0-13).<br>Select a level of -1 to exclude from search.</html>");
            // //Radio Button for less than
            // JRadioButton lvlLt = new JRadioButton("<", false);
            // lvlLt.setOpaque(false);
            // lvlLt.setFont(mainFont.deriveFont(22f));
            // lvlLt.setForeground(Color.WHITE);
            // lvlLt.setToolTipText("<html>Retrieves cards whose levels are less than the chosen value.</html>");
            // lvlLt.setFocusPainted(false);
            // //Radio Button for less than or equal to
            // JRadioButton lvlLte = new JRadioButton("<=", false);
            // lvlLte.setOpaque(false);
            // lvlLte.setFont(mainFont.deriveFont(22f));
            // lvlLte.setForeground(Color.WHITE);
            // lvlLte.setToolTipText("<html>Retrieves cards whose levels are less than or equal to the chosen value.</html>");
            // lvlLte.setFocusPainted(false);
            // //Radio Button for equal to
            // JRadioButton lvlEq = new JRadioButton("==", true);
            // lvlEq.setOpaque(false);
            // lvlEq.setFont(mainFont.deriveFont(22f));
            // lvlEq.setForeground(Color.WHITE);
            // lvlEq.setToolTipText("<html>Retrieves cards whose levels are equal to the chosen value.</html>");
            // lvlEq.setFocusPainted(false);
            // //Radio Button for greater than or equal to
            // JRadioButton lvlGte = new JRadioButton(">=", false);
            // lvlGte.setOpaque(false);
            // lvlGte.setFont(mainFont.deriveFont(22f));
            // lvlGte.setForeground(Color.WHITE);
            // lvlGte.setToolTipText("<html>Retrieves cards whose levels are greater than or equal to the chosen value.</html>");
            // lvlGte.setFocusPainted(false);
            // //Radio Button for greater than
            // JRadioButton lvlGt = new JRadioButton(">", false);
            // lvlGt.setOpaque(false);
            // lvlGt.setFont(mainFont.deriveFont(22f));
            // lvlGt.setForeground(Color.WHITE);
            // lvlGt.setToolTipText("<html>Retrieves cards whose levels are greater than the chosen value.</html>");
            // lvlGt.setFocusPainted(false);
            // //Adding
            // srchPnl.add(lvlRnkLabel);
            // srchPnl.add(horizSepTwo);
            // srchPnl.add(lvlRnk);
            // srchPnl.add(lvlLt);
            // srchPnl.add(lvlLte);
            // srchPnl.add(lvlEq);
            // srchPnl.add(lvlGte);
            // srchPnl.add(lvlGt);
            // //UI Layout
            // searchLayout.putConstraint(SpringLayout.WEST, horizSepTwo, 0, SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, horizSepTwo, (int)(waterLab.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, waterLab);
            // searchLayout.putConstraint(SpringLayout.WEST, lvlRnkLabel, 5, SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, lvlRnkLabel, (int)(horizSepTwo.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepTwo);

            // searchLayout.putConstraint(SpringLayout.WEST, lvlRnk, (int)(horizSepTwo.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, lvlRnk, (int)(lvlRnkLabel.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnkLabel);

            // searchLayout.putConstraint(SpringLayout.WEST, lvlLt, (int)(horizSepTwo.getPreferredSize().getWidth()/6), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, lvlLt, (int)(lvlRnk.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnk);

            // searchLayout.putConstraint(SpringLayout.WEST, lvlLte, (int)(horizSepTwo.getPreferredSize().getWidth()/3), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, lvlLte, (int)(lvlRnk.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnk);

            // searchLayout.putConstraint(SpringLayout.WEST, lvlEq, (int)(horizSepTwo.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, lvlEq, (int)(lvlRnk.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnk);

            // searchLayout.putConstraint(SpringLayout.WEST, lvlGte, (int)(horizSepTwo.getPreferredSize().getWidth() * 2/3), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, lvlGte, (int)(lvlRnk.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnk);

            // searchLayout.putConstraint(SpringLayout.WEST, lvlGt, (int)(horizSepTwo.getPreferredSize().getWidth() * 5/6), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, lvlGt, (int)(lvlRnk.getPreferredSize().getHeight()), SpringLayout.NORTH, lvlRnk);
            // //Make lvlRnkComparison button group
            // ButtonGroup lvlRnkComparisonButtonGroup = new ButtonGroup();
            // lvlRnkComparisonButtonGroup.add(lvlLt);
            // lvlRnkComparisonButtonGroup.add(lvlLte);
            // lvlRnkComparisonButtonGroup.add(lvlEq);
            // lvlRnkComparisonButtonGroup.add(lvlGte);
            // lvlRnkComparisonButtonGroup.add(lvlGt);

            // /* ----- LINK RATING SELECTION ----- */
            // //Horizontal Separator from Level Search
            // JSeparator horizSepThree = new JSeparator(JSeparator.HORIZONTAL);
            // horizSepThree.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            // horizSepThree.setBackground(Color.BLACK);
            // horizSepThree.setForeground(Color.RED);
            // //Label for link rating section
            // JLabel linkRateLabel = new JLabel("Link Rating:");
            // linkRateLabel.setFont(mainFont.deriveFont(22f));
            // linkRateLabel.setForeground(Color.WHITE);
            // //LINK RATING SPINNER
            // SpinnerModel linkRateLimit = new SpinnerNumberModel(0, 0, 8, 1);
            // JSpinner linkRate = new JSpinner(linkRateLimit);
            // linkRate.setFont(cardDetailsFont.deriveFont(22f));
            // linkRate.setToolTipText("<html>Search by Link Rating (1-8).<br>Select a rating of 0 to exclude from search.</html>");
            // Component linkEditor = linkRate.getEditor();
            // JFormattedTextField linkJftf = ((JSpinner.DefaultEditor) linkEditor).getTextField();
            // linkJftf.setColumns(2);
            // //Radio Button for less than
            // JRadioButton linkRateLt = new JRadioButton("<", false);
            // linkRateLt.setOpaque(false);
            // linkRateLt.setFont(mainFont.deriveFont(22f));
            // linkRateLt.setForeground(Color.WHITE);
            // linkRateLt.setToolTipText("<html>Retrieves cards whose link ratings are less than the chosen value.</html>");
            // linkRateLt.setFocusPainted(false);
            // //Radio Button for less than or equal to
            // JRadioButton linkRateLte = new JRadioButton("<=", false);
            // linkRateLte.setOpaque(false);
            // linkRateLte.setFont(mainFont.deriveFont(22f));
            // linkRateLte.setForeground(Color.WHITE);
            // linkRateLte.setToolTipText("<html>Retrieves cards whose link ratings are less than or equal to the chosen value.</html>");
            // linkRateLte.setFocusPainted(false);
            // //Radio Button for equal to
            // JRadioButton linkRateEq = new JRadioButton("==", true);
            // linkRateEq.setOpaque(false);
            // linkRateEq.setFont(mainFont.deriveFont(22f));
            // linkRateEq.setForeground(Color.WHITE);
            // linkRateEq.setToolTipText("<html>Retrieves cards whose link ratings are equal to the chosen value.</html>");
            // linkRateEq.setFocusPainted(false);
            // //Radio Button for greater than or equal to
            // JRadioButton linkRateGte = new JRadioButton(">=", false);
            // linkRateGte.setOpaque(false);
            // linkRateGte.setFont(mainFont.deriveFont(22f));
            // linkRateGte.setForeground(Color.WHITE);
            // linkRateGte.setToolTipText("<html>Retrieves cards whose link ratings are greater than or equal to the chosen value.</html>");
            // linkRateGte.setFocusPainted(false);
            // //Radio Button for greater than
            // JRadioButton linkRateGt = new JRadioButton(">", false);
            // linkRateGt.setOpaque(false);
            // linkRateGt.setFont(mainFont.deriveFont(22f));
            // linkRateGt.setForeground(Color.WHITE);
            // linkRateGt.setToolTipText("<html>Retrieves cards whose link ratings are greater than the chosen value.</html>");
            // linkRateGt.setFocusPainted(false);
            // //Adding
            // srchPnl.add(linkRateLabel);
            // srchPnl.add(horizSepThree);
            // srchPnl.add(linkRate);
            // srchPnl.add(linkRateLt);
            // srchPnl.add(linkRateLte);
            // srchPnl.add(linkRateEq);
            // srchPnl.add(linkRateGte);
            // srchPnl.add(linkRateGt);
            // //UI Layout
            // searchLayout.putConstraint(SpringLayout.WEST, horizSepThree, 0, SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, horizSepThree, (int)(lvlEq.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, lvlEq);
            // searchLayout.putConstraint(SpringLayout.WEST, linkRateLabel, 5, SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, linkRateLabel, (int)(horizSepThree.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepThree);

            // searchLayout.putConstraint(SpringLayout.WEST, linkRate, (int)(horizSepThree.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, linkRate, (int)(linkRateLabel.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRateLabel);

            // searchLayout.putConstraint(SpringLayout.WEST, linkRateLt, (int)(horizSepThree.getPreferredSize().getWidth()/6), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, linkRateLt, (int)(linkRate.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRate);

            // searchLayout.putConstraint(SpringLayout.WEST, linkRateLte, (int)(horizSepThree.getPreferredSize().getWidth()/3), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, linkRateLte, (int)(linkRate.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRate);

            // searchLayout.putConstraint(SpringLayout.WEST, linkRateEq, (int)(horizSepThree.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, linkRateEq, (int)(linkRate.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRate);

            // searchLayout.putConstraint(SpringLayout.WEST, linkRateGte, (int)(horizSepThree.getPreferredSize().getWidth() * 2/3), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, linkRateGte, (int)(linkRate.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRate);

            // searchLayout.putConstraint(SpringLayout.WEST, linkRateGt, (int)(horizSepThree.getPreferredSize().getWidth() * 5/6), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, linkRateGt, (int)(linkRate.getPreferredSize().getHeight()), SpringLayout.NORTH, linkRate);
            // //Make linkRateComparison button group
            // ButtonGroup linkRateComparisonButtonGroup = new ButtonGroup();
            // linkRateComparisonButtonGroup.add(linkRateLt);
            // linkRateComparisonButtonGroup.add(linkRateLte);
            // linkRateComparisonButtonGroup.add(linkRateEq);
            // linkRateComparisonButtonGroup.add(linkRateGte);
            // linkRateComparisonButtonGroup.add(linkRateGt);

            // /* ----- PENDULUM SCALE SELECTION ----- */
            // //Horizontal Separator from Link Rating Search
            // JSeparator horizSepFour = new JSeparator(JSeparator.HORIZONTAL);
            // horizSepFour.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            // horizSepFour.setBackground(Color.BLACK);
            // horizSepFour.setForeground(Color.RED);
            // //Label for link rating section
            // JLabel pendScaleLabel = new JLabel("Pendulum Scale:");
            // pendScaleLabel.setFont(mainFont.deriveFont(22f));
            // pendScaleLabel.setForeground(Color.WHITE);
            // //LINK RATING SPINNER
            // SpinnerModel pendScaleLimit = new SpinnerNumberModel(-1, -1, 13, 1);
            // JSpinner pendScale = new JSpinner(pendScaleLimit);
            // pendScale.setFont(cardDetailsFont.deriveFont(22f));
            // pendScale.setToolTipText("<html>Search by Pendulum Scale (0-13).<br>Select a scale of -1 to exclude from search.</html>");
            // //Radio Button for less than
            // JRadioButton pendScaleLt = new JRadioButton("<", false);
            // pendScaleLt.setOpaque(false);
            // pendScaleLt.setFont(mainFont.deriveFont(22f));
            // pendScaleLt.setForeground(Color.WHITE);
            // pendScaleLt.setToolTipText("<html>Retrieves cards whose pendulum scales are less than the chosen value.</html>");
            // pendScaleLt.setFocusPainted(false);
            // //Radio Button for less than or equal to
            // JRadioButton pendScaleLte = new JRadioButton("<=", false);
            // pendScaleLte.setOpaque(false);
            // pendScaleLte.setFont(mainFont.deriveFont(22f));
            // pendScaleLte.setForeground(Color.WHITE);
            // pendScaleLte.setToolTipText("<html>Retrieves cards whose pendulum scales are less than or equal to the chosen value.</html>");
            // pendScaleLte.setFocusPainted(false);
            // //Radio Button for equal to
            // JRadioButton pendScaleEq = new JRadioButton("==", true);
            // pendScaleEq.setOpaque(false);
            // pendScaleEq.setFont(mainFont.deriveFont(22f));
            // pendScaleEq.setForeground(Color.WHITE);
            // pendScaleEq.setToolTipText("<html>Retrieves cards whose pendulum scales are equal to the chosen value.</html>");
            // pendScaleEq.setFocusPainted(false);
            // //Radio Button for greater than or equal to
            // JRadioButton pendScaleGte = new JRadioButton(">=", false);
            // pendScaleGte.setOpaque(false);
            // pendScaleGte.setFont(mainFont.deriveFont(22f));
            // pendScaleGte.setForeground(Color.WHITE);
            // pendScaleGte.setToolTipText("<html>Retrieves cards whose pendulum scales are greater than or equal to the chosen value.</html>");
            // pendScaleGte.setFocusPainted(false);
            // //Radio Button for greater than
            // JRadioButton pendScaleGt = new JRadioButton(">", false);
            // pendScaleGt.setOpaque(false);
            // pendScaleGt.setFont(mainFont.deriveFont(22f));
            // pendScaleGt.setForeground(Color.WHITE);
            // pendScaleGt.setToolTipText("<html>Retrieves cards whose pendulum scales are greater than the chosen value.</html>");
            // pendScaleGt.setFocusPainted(false);
            // //Adding
            // srchPnl.add(pendScaleLabel);
            // srchPnl.add(horizSepFour);
            // srchPnl.add(pendScale);
            // srchPnl.add(pendScaleLt);
            // srchPnl.add(pendScaleLte);
            // srchPnl.add(pendScaleEq);
            // srchPnl.add(pendScaleGte);
            // srchPnl.add(pendScaleGt);
            // //UI Layout
            // searchLayout.putConstraint(SpringLayout.WEST, horizSepFour, 0, SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, horizSepFour, (int)(linkRateEq.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, linkRateEq);

            // searchLayout.putConstraint(SpringLayout.WEST, pendScaleLabel, 5, SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, pendScaleLabel, (int)(horizSepFour.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepFour);

            // searchLayout.putConstraint(SpringLayout.WEST, pendScale, (int)(horizSepFour.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, pendScale, (int)(pendScaleLabel.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScaleLabel);

            // searchLayout.putConstraint(SpringLayout.WEST, pendScaleLt, (int)(horizSepFour.getPreferredSize().getWidth()/6), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, pendScaleLt, (int)(pendScale.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScale);

            // searchLayout.putConstraint(SpringLayout.WEST, pendScaleLte, (int)(horizSepFour.getPreferredSize().getWidth()/3), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, pendScaleLte, (int)(pendScale.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScale);

            // searchLayout.putConstraint(SpringLayout.WEST, pendScaleEq, (int)(horizSepFour.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, pendScaleEq, (int)(pendScale.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScale);

            // searchLayout.putConstraint(SpringLayout.WEST, pendScaleGte, (int)(horizSepFour.getPreferredSize().getWidth() * 2/3), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, pendScaleGte, (int)(pendScale.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScale);

            // searchLayout.putConstraint(SpringLayout.WEST, pendScaleGt, (int)(horizSepFour.getPreferredSize().getWidth() * 5/6), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, pendScaleGt, (int)(pendScale.getPreferredSize().getHeight()), SpringLayout.NORTH, pendScale);
            // //Make linkRateComparison button group
            // ButtonGroup pendScaleComparisonButtonGroup = new ButtonGroup();
            // pendScaleComparisonButtonGroup.add(pendScaleLt);
            // pendScaleComparisonButtonGroup.add(pendScaleLte);
            // pendScaleComparisonButtonGroup.add(pendScaleEq);
            // pendScaleComparisonButtonGroup.add(pendScaleGte);
            // pendScaleComparisonButtonGroup.add(pendScaleGt);

            // /* ----- LINK MARKER SELECTION ----- */
            // //Label for link rating section
            // JLabel linkArrowLabel = new JLabel("Link Arrows:");
            // linkArrowLabel.setFont(mainFont.deriveFont(22f));
            // linkArrowLabel.setForeground(Color.WHITE);
            // //Up Link Arrow
            // JCheckBox uArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkuoff.png"));
            // uArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkuon.png"));
            // uArrow.setToolTipText("Select to search for cards that have a top Link Arrow.");
            // uArrow.setOpaque(false);
            // //Up-Right Link Arrow
            // JCheckBox uRArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkuroff.png"));
            // uRArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkuron.png"));
            // uRArrow.setToolTipText("Select to search for cards that have a top-right Link Arrow.");
            // uRArrow.setOpaque(false);
            // //Up-Left Link Arrow
            // JCheckBox uLArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkuloff.png"));
            // uLArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkulon.png"));
            // uLArrow.setToolTipText("Select to search for cards that have a top-left Link Arrow.");
            // uLArrow.setOpaque(false);
            // //Left Link Arrow
            // JCheckBox lArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkloff.png"));
            // lArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linklon.png"));
            // lArrow.setToolTipText("Select to search for cards that have a left Link Arrow.");
            // lArrow.setOpaque(false);
            // //Right Link Arrow
            // JCheckBox rArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkroff.png"));
            // rArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkron.png"));
            // rArrow.setToolTipText("Select to search for cards that have a right Link Arrow.");
            // rArrow.setOpaque(false);
            // //Bottom Link Arrow
            // JCheckBox bArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkdoff.png"));
            // bArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkdon.png"));
            // bArrow.setToolTipText("Select to search for cards that have a bottom Link Arrow.");
            // bArrow.setOpaque(false);
            // //Up-Right Link Arrow
            // JCheckBox bRArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkdroff.png"));
            // bRArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkdron.png"));
            // bRArrow.setToolTipText("Select to search for cards that have a bottom-right Link Arrow.");
            // bRArrow.setOpaque(false);
            // //Up-Left Link Arrow
            // JCheckBox bLArrow = new JCheckBox(new ImageIcon("img/cardInfo/linkdloff.png"));
            // bLArrow.setSelectedIcon(new ImageIcon("img/cardInfo/linkdlon.png"));
            // bLArrow.setToolTipText("Select to search for cards that have a bottom-left Link Arrow.");
            // bLArrow.setOpaque(false);
            // //Adding
            // srchPnl.add(linkArrowLabel);
            // srchPnl.add(uArrow);
            // srchPnl.add(uLArrow);
            // srchPnl.add(uRArrow);
            // srchPnl.add(rArrow);
            // srchPnl.add(bRArrow);
            // srchPnl.add(bArrow);
            // srchPnl.add(bLArrow);
            // srchPnl.add(lArrow);
            // //UI Layout
            // searchLayout.putConstraint(SpringLayout.WEST, linkArrowLabel, (int)(srchPnl.getPreferredSize().getWidth()/2 + 5), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, linkArrowLabel, 5, SpringLayout.NORTH, srchPnl);

            // searchLayout.putConstraint(SpringLayout.WEST, uLArrow, (int)(linkArrowLabel.getPreferredSize().getWidth() + 5), SpringLayout.WEST, linkArrowLabel);
            // searchLayout.putConstraint(SpringLayout.NORTH, uLArrow, 5, SpringLayout.NORTH, linkArrowLabel);

            // searchLayout.putConstraint(SpringLayout.WEST, uArrow, (int)(uLArrow.getPreferredSize().getWidth() + 5), SpringLayout.WEST, uLArrow);
            // searchLayout.putConstraint(SpringLayout.NORTH, uArrow, 5, SpringLayout.NORTH, uLArrow);

            // searchLayout.putConstraint(SpringLayout.WEST, uRArrow, (int)(uArrow.getPreferredSize().getWidth() + 5), SpringLayout.WEST, uArrow);
            // searchLayout.putConstraint(SpringLayout.NORTH, uRArrow, 0, SpringLayout.NORTH, uLArrow);

            // searchLayout.putConstraint(SpringLayout.WEST, lArrow, (int)(linkArrowLabel.getPreferredSize().getWidth() + 5), SpringLayout.WEST, linkArrowLabel);
            // searchLayout.putConstraint(SpringLayout.NORTH, lArrow, (int)(uLArrow.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, uLArrow);

            // searchLayout.putConstraint(SpringLayout.WEST, rArrow, (int)(uArrow.getPreferredSize().getWidth() + uRArrow.getPreferredSize().getWidth()/2), SpringLayout.WEST, uArrow);
            // searchLayout.putConstraint(SpringLayout.NORTH, rArrow, (int)(uRArrow.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, uRArrow);

            // searchLayout.putConstraint(SpringLayout.WEST, bLArrow, (int)(linkArrowLabel.getPreferredSize().getWidth() + 5), SpringLayout.WEST, linkArrowLabel);
            // searchLayout.putConstraint(SpringLayout.NORTH, bLArrow, (int)(uLArrow.getPreferredSize().getHeight() + lArrow.getPreferredSize().getHeight() + 10), SpringLayout.NORTH, uRArrow);

            // searchLayout.putConstraint(SpringLayout.WEST, bArrow, (int)(bLArrow.getPreferredSize().getWidth() + 5), SpringLayout.WEST, bLArrow);
            // searchLayout.putConstraint(SpringLayout.NORTH, bArrow, (int)(bLArrow.getPreferredSize().getHeight() - (bArrow.getPreferredSize().getHeight() + 5)), SpringLayout.NORTH, bLArrow);

            // searchLayout.putConstraint(SpringLayout.WEST, bRArrow, (int)(bArrow.getPreferredSize().getWidth() + 5), SpringLayout.WEST, bArrow);
            // searchLayout.putConstraint(SpringLayout.NORTH, bRArrow, 0, SpringLayout.NORTH, bLArrow);

            // /* ----- TYPE SELECTION ----- */
            // //Separator from Link Arrow Selection
            // JSeparator horizSepFive = new JSeparator(JSeparator.HORIZONTAL);
            // horizSepFive.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            // horizSepFive.setBackground(Color.BLACK);
            // horizSepFive.setForeground(Color.RED);
            // //Type Label
            // JLabel typeLabel = new JLabel("Type:");
            // typeLabel.setFont(mainFont.deriveFont(22f));
            // typeLabel.setForeground(Color.WHITE);
            // //JList model
            // DefaultListModel<String> monsDlm = new DefaultListModel<String>();
            // for(int i = 0;i < MONSTER_TYPES.length; i++) {
            //     monsDlm.add(i, MONSTER_TYPES[i]);
            // }
            // //JList for monster types
            // JList<String> monsType = new JList<String>(monsDlm);
            // monsType.setLayoutOrientation(JList.VERTICAL_WRAP);
            // monsType.setVisibleRowCount(13);
            // monsType.setFont(cardDetailsFont.deriveFont(22f));
            // monsType.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            // monsType.setBackground(new Color(88, 17, 17));
            // monsType.setForeground(Color.WHITE);
            // //Scroller for JList
            // JScrollPane monsTypeScroller = new JScrollPane(monsType);
            // monsTypeScroller.setPreferredSize(new Dimension(300, 175));
            // //Checkboxes per type
            // //CONTINUOUS CHECKBOX
            // JCheckBox continuous = new JCheckBox();
            // continuous.setOpaque(false);
            // JLabel contLab = new JLabel("Continuous", new ImageIcon("img/races/contOpt.png"), JLabel.LEFT);
            // contLab.setFont(cardSetFont.deriveFont(22f));
            // contLab.setForeground(Color.WHITE);
            // continuous.setMnemonic(KeyEvent.VK_C);
            // continuous.setSelected(false);
            // continuous.setToolTipText("<html>Select to search for <i>CONTINUOUS</i> spells or traps.<br>Shortcut: Alt+C.</html>");
            // //COUNTER CHECKBOX
            // JCheckBox counter = new JCheckBox();
            // counter.setOpaque(false);
            // JLabel countLab = new JLabel("Counter", new ImageIcon("img/races/countOpt.png"), JLabel.LEFT);
            // countLab.setFont(cardSetFont.deriveFont(22f));
            // countLab.setForeground(Color.WHITE);
            // counter.setMnemonic(KeyEvent.VK_U);
            // counter.setSelected(false);
            // counter.setToolTipText("<html>Select to search for <i>COUNTER</i> traps.<br>Shortcut: Alt+U.</html>");
            // //EQUIP CHECKBOX
            // JCheckBox equip = new JCheckBox();
            // equip.setOpaque(false);
            // JLabel equipLab = new JLabel("Equip", new ImageIcon("img/races/equipOpt.png"), JLabel.LEFT);
            // equipLab.setFont(cardSetFont.deriveFont(22f));
            // equipLab.setForeground(Color.WHITE);
            // equip.setMnemonic(KeyEvent.VK_Q);
            // equip.setSelected(false);
            // equip.setToolTipText("<html>Select to search for <i>EQUIP</i> spells.<br>Shortcut: Alt+Q.</html>");
            // //FIELD CHECKBOX
            // JCheckBox field = new JCheckBox();
            // field.setOpaque(false);
            // JLabel fieldLab = new JLabel("Field", new ImageIcon("img/races/fieldOpt.png"), JLabel.LEFT);
            // fieldLab.setFont(cardSetFont.deriveFont(22f));
            // fieldLab.setForeground(Color.WHITE);
            // field.setMnemonic(KeyEvent.VK_A);
            // field.setSelected(false);
            // field.setToolTipText("<html>Select to search for <i>FIELD</i> spells.<br>Shortcut: Alt+A.</html>");
            // //QUICK-PLAY CHECKBOX
            // JCheckBox quickPlay = new JCheckBox();
            // quickPlay.setOpaque(false);
            // JLabel qpLab = new JLabel("Quick-Play", new ImageIcon("img/races/qpOpt.png"), JLabel.LEFT);
            // qpLab.setFont(cardSetFont.deriveFont(22f));
            // qpLab.setForeground(Color.WHITE);
            // quickPlay.setMnemonic(KeyEvent.VK_P);
            // quickPlay.setSelected(false);
            // quickPlay.setToolTipText("<html>Select to search for <i>QUICK-PLAY</i> spells.<br>Shortcut: Alt+Q.</html>");
            // //RITUAL CHECKBOX
            // JCheckBox ritual = new JCheckBox();
            // ritual.setOpaque(false);
            // JLabel ritLab = new JLabel("Ritual", new ImageIcon("img/races/ritOpt.png"), JLabel.LEFT);
            // ritLab.setFont(cardSetFont.deriveFont(22f));
            // ritLab.setForeground(Color.WHITE);
            // ritual.setMnemonic(KeyEvent.VK_R);
            // ritual.setSelected(false);
            // ritual.setToolTipText("<html>Select to search for <i>RITUAL</i> spells.<br>Shortcut: Alt+R.</html>");
            // //NORMAL CHECKBOX
            // JCheckBox normal = new JCheckBox();
            // normal.setOpaque(false);
            // JLabel normLab = new JLabel("Normal");
            // normLab.setFont(cardSetFont.deriveFont(22f));
            // normLab.setForeground(Color.WHITE);
            // normal.setMnemonic(KeyEvent.VK_O);
            // normal.setSelected(false);
            // normal.setToolTipText("<html>Select to search for <i>NORMAL</i> spells or traps.<br>Shortcut: Alt+O.</html>");
            // //Adding
            // srchPnl.add(horizSepFive);
            // srchPnl.add(typeLabel);
            // srchPnl.add(monsTypeScroller);
            // srchPnl.add(continuous);
            // srchPnl.add(contLab);
            // srchPnl.add(counter);
            // srchPnl.add(countLab);
            // srchPnl.add(equip);
            // srchPnl.add(equipLab);
            // srchPnl.add(field);
            // srchPnl.add(fieldLab);
            // srchPnl.add(quickPlay);
            // srchPnl.add(qpLab);
            // srchPnl.add(ritual);
            // srchPnl.add(ritLab);
            // srchPnl.add(normal);
            // srchPnl.add(normLab);
            // //UI Layout
            // searchLayout.putConstraint(SpringLayout.WEST, horizSepFive, (int)(srchPnl.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, horizSepFive, (int)(bLArrow.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, bLArrow);

            // searchLayout.putConstraint(SpringLayout.WEST, typeLabel, (int)(srchPnl.getPreferredSize().getWidth()/2 + 5), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, typeLabel, (int)(horizSepFive.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepFive);

            // searchLayout.putConstraint(SpringLayout.WEST, monsTypeScroller, (int)(srchPnl.getPreferredSize().getWidth()/2 + 25), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, monsTypeScroller, (int)(typeLabel.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, typeLabel);

            // searchLayout.putConstraint(SpringLayout.WEST, continuous, (int)(monsTypeScroller.getPreferredSize().getWidth() + 15), SpringLayout.WEST, monsTypeScroller);
            // searchLayout.putConstraint(SpringLayout.NORTH, continuous, (int)(typeLabel.getPreferredSize().getHeight() + contLab.getPreferredSize().getHeight()/2) - 5, SpringLayout.NORTH, typeLabel);
            // searchLayout.putConstraint(SpringLayout.WEST, contLab, (int)(continuous.getPreferredSize().getWidth() + 1), SpringLayout.WEST, continuous);
            // searchLayout.putConstraint(SpringLayout.NORTH, contLab, (int)(continuous.getPreferredSize().getHeight()/2 - contLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, continuous);

            // searchLayout.putConstraint(SpringLayout.WEST, quickPlay, 0, SpringLayout.WEST, continuous);
            // searchLayout.putConstraint(SpringLayout.NORTH, quickPlay, (int)(contLab.getPreferredSize().getHeight() + qpLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, contLab);
            // searchLayout.putConstraint(SpringLayout.WEST, qpLab, (int)(quickPlay.getPreferredSize().getWidth() + 1), SpringLayout.WEST, quickPlay);
            // searchLayout.putConstraint(SpringLayout.NORTH, qpLab, (int)(quickPlay.getPreferredSize().getHeight()/2 - qpLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, quickPlay);

            // searchLayout.putConstraint(SpringLayout.WEST, equip, 0, SpringLayout.WEST, continuous);
            // searchLayout.putConstraint(SpringLayout.NORTH, equip, (int)(qpLab.getPreferredSize().getHeight() + equipLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, qpLab);
            // searchLayout.putConstraint(SpringLayout.WEST, equipLab, (int)(equip.getPreferredSize().getWidth() + 1), SpringLayout.WEST, equip);
            // searchLayout.putConstraint(SpringLayout.NORTH, equipLab, (int)(equip.getPreferredSize().getHeight()/2 - equipLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, equip);

            // searchLayout.putConstraint(SpringLayout.WEST, field, 0, SpringLayout.WEST, continuous);
            // searchLayout.putConstraint(SpringLayout.NORTH, field, (int)(equipLab.getPreferredSize().getHeight() + fieldLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, equipLab);
            // searchLayout.putConstraint(SpringLayout.WEST, fieldLab, (int)(field.getPreferredSize().getWidth() + 1), SpringLayout.WEST, field);
            // searchLayout.putConstraint(SpringLayout.NORTH, fieldLab, (int)(field.getPreferredSize().getHeight()/2 - fieldLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, field);

            // searchLayout.putConstraint(SpringLayout.WEST, counter, (int)(qpLab.getPreferredSize().getWidth()), SpringLayout.WEST, qpLab);
            // searchLayout.putConstraint(SpringLayout.NORTH, counter, (int)(-quickPlay.getPreferredSize().getHeight()), SpringLayout.NORTH, quickPlay);
            // searchLayout.putConstraint(SpringLayout.WEST, countLab, (int)(counter.getPreferredSize().getWidth() + 1), SpringLayout.WEST, counter);
            // searchLayout.putConstraint(SpringLayout.NORTH, countLab, (int)(counter.getPreferredSize().getHeight()/2 - countLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, counter);
            
            // searchLayout.putConstraint(SpringLayout.WEST, ritual, 0, SpringLayout.WEST, counter);
            // searchLayout.putConstraint(SpringLayout.NORTH, ritual, (int)(countLab.getPreferredSize().getHeight() + ritLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, counter);
            // searchLayout.putConstraint(SpringLayout.WEST, ritLab, (int)(ritual.getPreferredSize().getWidth() + 1), SpringLayout.WEST, ritual);
            // searchLayout.putConstraint(SpringLayout.NORTH, ritLab, (int)(ritual.getPreferredSize().getHeight()/2 - ritLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, ritual);

            // searchLayout.putConstraint(SpringLayout.WEST, normal, 0, SpringLayout.WEST, counter);
            // searchLayout.putConstraint(SpringLayout.NORTH, normal, (int)(ritLab.getPreferredSize().getHeight() + normLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, ritual);
            // searchLayout.putConstraint(SpringLayout.WEST, normLab, (int)(normal.getPreferredSize().getWidth() + 1), SpringLayout.WEST, normal);
            // searchLayout.putConstraint(SpringLayout.NORTH, normLab, (int)(normal.getPreferredSize().getHeight()/2 - normLab.getPreferredSize().getHeight()/2), SpringLayout.NORTH, normal);

            // /* ----- ATTACK SELECTION ----- */
            // //Horizontal Separator from Link Rating Search
            // JSeparator horizSepSix = new JSeparator(JSeparator.HORIZONTAL);
            // horizSepSix.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            // horizSepSix.setBackground(Color.BLACK);
            // horizSepSix.setForeground(Color.RED);
            // //Label for link rating section
            // JLabel atkLabel = new JLabel("Attack:");
            // atkLabel.setFont(mainFont.deriveFont(22f));
            // atkLabel.setForeground(Color.WHITE);
            // //LINK RATING SPINNER
            // SpinnerModel atkLimit = new SpinnerNumberModel(-50, -50, 5000, 50);
            // JSpinner atk = new JSpinner(atkLimit);
            // atk.setFont(cardDetailsFont.deriveFont(22f));
            // atk.setToolTipText("<html>Search by Attack (0-5000).<br>Select an attack of -50 to exclude from search.</html>");
            // //Radio Button for less than
            // JRadioButton atkLt = new JRadioButton("<", false);
            // atkLt.setOpaque(false);
            // atkLt.setFont(mainFont.deriveFont(22f));
            // atkLt.setForeground(Color.WHITE);
            // atkLt.setToolTipText("<html>Retrieves cards whose attack values are less than the chosen value.</html>");
            // atkLt.setFocusPainted(false);
            // //Radio Button for less than or equal to
            // JRadioButton atkLte = new JRadioButton("<=", false);
            // atkLte.setOpaque(false);
            // atkLte.setFont(mainFont.deriveFont(22f));
            // atkLte.setForeground(Color.WHITE);
            // atkLte.setToolTipText("<html>Retrieves cards whose attack values are less than or equal to the chosen value.</html>");
            // atkLte.setFocusPainted(false);
            // //Radio Button for equal to
            // JRadioButton atkEq = new JRadioButton("==", true);
            // atkEq.setOpaque(false);
            // atkEq.setFont(mainFont.deriveFont(22f));
            // atkEq.setForeground(Color.WHITE);
            // atkEq.setToolTipText("<html>Retrieves cards whose attack values are equal to the chosen value.</html>");
            // atkEq.setFocusPainted(false);
            // //Radio Button for greater than or equal to
            // JRadioButton atkGte = new JRadioButton(">=", false);
            // atkGte.setOpaque(false);
            // atkGte.setFont(mainFont.deriveFont(22f));
            // atkGte.setForeground(Color.WHITE);
            // atkGte.setToolTipText("<html>Retrieves cards whose attack values are greater than or equal to the chosen value.</html>");
            // atkGte.setFocusPainted(false);
            // //Radio Button for greater than
            // JRadioButton atkGt = new JRadioButton(">", false);
            // atkGt.setOpaque(false);
            // atkGt.setFont(mainFont.deriveFont(22f));
            // atkGt.setForeground(Color.WHITE);
            // atkGt.setToolTipText("<html>Retrieves cards whose attack values are greater than the chosen value.</html>");
            // atkGt.setFocusPainted(false);
            // //Adding
            // srchPnl.add(atkLabel);
            // srchPnl.add(horizSepSix);
            // srchPnl.add(atk);
            // srchPnl.add(atkLt);
            // srchPnl.add(atkLte);
            // srchPnl.add(atkEq);
            // srchPnl.add(atkGte);
            // srchPnl.add(atkGt);
            // //UI Layout
            // searchLayout.putConstraint(SpringLayout.WEST, horizSepSix, (int)(srchPnl.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, horizSepSix, (int)(monsTypeScroller.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, monsTypeScroller);

            // searchLayout.putConstraint(SpringLayout.WEST, atkLabel, (int)(srchPnl.getPreferredSize().getWidth()/2 + 5), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, atkLabel, (int)(horizSepSix.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepSix);

            // searchLayout.putConstraint(SpringLayout.WEST, atk, (int)(horizSepSix.getPreferredSize().getWidth()/2 + srchPnl.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, atk, (int)(atkLabel.getPreferredSize().getHeight()), SpringLayout.NORTH, atkLabel);

            // searchLayout.putConstraint(SpringLayout.WEST, atkLt, (int)(horizSepSix.getPreferredSize().getWidth()/6), SpringLayout.WEST, horizSepSix);
            // searchLayout.putConstraint(SpringLayout.NORTH, atkLt, (int)(atk.getPreferredSize().getHeight()), SpringLayout.NORTH, atk);

            // searchLayout.putConstraint(SpringLayout.WEST, atkLte, (int)(horizSepSix.getPreferredSize().getWidth()/3), SpringLayout.WEST, horizSepSix);
            // searchLayout.putConstraint(SpringLayout.NORTH, atkLte, (int)(atk.getPreferredSize().getHeight()), SpringLayout.NORTH, atk);

            // searchLayout.putConstraint(SpringLayout.WEST, atkEq, (int)(horizSepSix.getPreferredSize().getWidth()/2), SpringLayout.WEST, horizSepSix);
            // searchLayout.putConstraint(SpringLayout.NORTH, atkEq, (int)(atk.getPreferredSize().getHeight()), SpringLayout.NORTH, atk);

            // searchLayout.putConstraint(SpringLayout.WEST, atkGte, (int)(horizSepSix.getPreferredSize().getWidth() * 2/3), SpringLayout.WEST, horizSepSix);
            // searchLayout.putConstraint(SpringLayout.NORTH, atkGte, (int)(atk.getPreferredSize().getHeight()), SpringLayout.NORTH, atk);

            // searchLayout.putConstraint(SpringLayout.WEST, atkGt, (int)(horizSepSix.getPreferredSize().getWidth() * 5/6), SpringLayout.WEST, horizSepSix);
            // searchLayout.putConstraint(SpringLayout.NORTH, atkGt, (int)(atk.getPreferredSize().getHeight()), SpringLayout.NORTH, atk);
            // //Make linkRateComparison button group
            // ButtonGroup atkComparisonButtonGroup = new ButtonGroup();
            // atkComparisonButtonGroup.add(atkLt);
            // atkComparisonButtonGroup.add(atkLte);
            // atkComparisonButtonGroup.add(atkEq);
            // atkComparisonButtonGroup.add(atkGte);
            // atkComparisonButtonGroup.add(atkGt);

            // /* ----- ATTACK SELECTION ----- */
            // //Horizontal Separator from Link Rating Search
            // JSeparator horizSepSeven = new JSeparator(JSeparator.HORIZONTAL);
            // horizSepSeven.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()/2), 5));
            // horizSepSeven.setBackground(Color.BLACK);
            // horizSepSeven.setForeground(Color.RED);
            // //Label for link rating section
            // JLabel defLabel = new JLabel("Defense:");
            // defLabel.setFont(mainFont.deriveFont(22f));
            // defLabel.setForeground(Color.WHITE);
            // //LINK RATING SPINNER
            // SpinnerModel defLimit = new SpinnerNumberModel(-50, -50, 5000, 50);
            // JSpinner def = new JSpinner(defLimit);
            // def.setFont(cardDetailsFont.deriveFont(22f));
            // def.setToolTipText("<html>Search by Defense (0-5000).<br>Select a defense of -50 to exclude from search.</html>");
            // //Radio Button for less than
            // JRadioButton defLt = new JRadioButton("<", false);
            // defLt.setOpaque(false);
            // defLt.setFont(mainFont.deriveFont(22f));
            // defLt.setForeground(Color.WHITE);
            // defLt.setToolTipText("<html>Retrieves cards whose defense values are less than the chosen value.</html>");
            // defLt.setFocusPainted(false);
            // //Radio Button for less than or equal to
            // JRadioButton defLte = new JRadioButton("<=", false);
            // defLte.setOpaque(false);
            // defLte.setFont(mainFont.deriveFont(22f));
            // defLte.setForeground(Color.WHITE);
            // defLte.setToolTipText("<html>Retrieves cards whose defense values are less than or equal to the chosen value.</html>");
            // defLte.setFocusPainted(false);
            // //Radio Button for equal to
            // JRadioButton defEq = new JRadioButton("==", true);
            // defEq.setOpaque(false);
            // defEq.setFont(mainFont.deriveFont(22f));
            // defEq.setForeground(Color.WHITE);
            // defEq.setToolTipText("<html>Retrieves cards whose defense values are equal to the chosen value.</html>");
            // defEq.setFocusPainted(false);
            // //Radio Button for greater than or equal to
            // JRadioButton defGte = new JRadioButton(">=", false);
            // defGte.setOpaque(false);
            // defGte.setFont(mainFont.deriveFont(22f));
            // defGte.setForeground(Color.WHITE);
            // defGte.setToolTipText("<html>Retrieves cards whose defense values are greater than or equal to the chosen value.</html>");
            // defGte.setFocusPainted(false);
            // //Radio Button for greater than
            // JRadioButton defGt = new JRadioButton(">", false);
            // defGt.setOpaque(false);
            // defGt.setFont(mainFont.deriveFont(22f));
            // defGt.setForeground(Color.WHITE);
            // defGt.setToolTipText("<html>Retrieves cards whose defense values are greater than the chosen value.</html>");
            // defGt.setFocusPainted(false);
            // //Adding
            // srchPnl.add(defLabel);
            // srchPnl.add(horizSepSeven);
            // srchPnl.add(def);
            // srchPnl.add(defLt);
            // srchPnl.add(defLte);
            // srchPnl.add(defEq);
            // srchPnl.add(defGte);
            // srchPnl.add(defGt);
            // //UI Layout
            // searchLayout.putConstraint(SpringLayout.WEST, horizSepSeven, (int)(srchPnl.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, horizSepSeven, (int)(atkEq.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, atkEq);

            // searchLayout.putConstraint(SpringLayout.WEST, defLabel, (int)(srchPnl.getPreferredSize().getWidth()/2 + 5), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, defLabel, (int)(horizSepSeven.getPreferredSize().getHeight()), SpringLayout.NORTH, horizSepSeven);

            // searchLayout.putConstraint(SpringLayout.WEST, def, (int)(horizSepSeven.getPreferredSize().getWidth()/2 + srchPnl.getPreferredSize().getWidth()/2), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, def, (int)(defLabel.getPreferredSize().getHeight()), SpringLayout.NORTH, defLabel);

            // searchLayout.putConstraint(SpringLayout.WEST, defLt, (int)(horizSepSeven.getPreferredSize().getWidth()/6), SpringLayout.WEST, horizSepSeven);
            // searchLayout.putConstraint(SpringLayout.NORTH, defLt, (int)(def.getPreferredSize().getHeight()), SpringLayout.NORTH, def);

            // searchLayout.putConstraint(SpringLayout.WEST, defLte, (int)(horizSepSeven.getPreferredSize().getWidth()/3), SpringLayout.WEST, horizSepSeven);
            // searchLayout.putConstraint(SpringLayout.NORTH, defLte, (int)(def.getPreferredSize().getHeight()), SpringLayout.NORTH, def);

            // searchLayout.putConstraint(SpringLayout.WEST, defEq, (int)(horizSepSeven.getPreferredSize().getWidth()/2), SpringLayout.WEST, horizSepSeven);
            // searchLayout.putConstraint(SpringLayout.NORTH, defEq, (int)(def.getPreferredSize().getHeight()), SpringLayout.NORTH, def);

            // searchLayout.putConstraint(SpringLayout.WEST, defGte, (int)(horizSepSeven.getPreferredSize().getWidth() * 2/3), SpringLayout.WEST, horizSepSeven);
            // searchLayout.putConstraint(SpringLayout.NORTH, defGte, (int)(def.getPreferredSize().getHeight()), SpringLayout.NORTH, def);

            // searchLayout.putConstraint(SpringLayout.WEST, defGt, (int)(horizSepSeven.getPreferredSize().getWidth() * 5/6), SpringLayout.WEST, horizSepSeven);
            // searchLayout.putConstraint(SpringLayout.NORTH, defGt, (int)(def.getPreferredSize().getHeight()), SpringLayout.NORTH, def);
            // //Make linkRateComparison button group
            // ButtonGroup defComparisonButtonGroup = new ButtonGroup();
            // defComparisonButtonGroup.add(defLt);
            // defComparisonButtonGroup.add(defLte);
            // defComparisonButtonGroup.add(defEq);
            // defComparisonButtonGroup.add(defGte);
            // defComparisonButtonGroup.add(defGt);

            // /* ----- FINAL SEPARATOR ----- */
            // JSeparator finalHorizSep = new JSeparator(JSeparator.HORIZONTAL);
            // finalHorizSep.setPreferredSize(new Dimension((int)(srchPnl.getPreferredSize().getWidth()), 5));
            // finalHorizSep.setBackground(Color.BLACK);
            // finalHorizSep.setForeground(Color.RED);
            
            // /* ----- SEARCH BUTTON ----- */
            // JButton search = new JButton("Search", new ImageIcon("img/system/srchIcon.png"));
            // search.setFont(mainFont.deriveFont(22f));
            // Dimension sizes = search.getPreferredSize();
            // sizes.width = (int)(srchPnl.getPreferredSize().getWidth() - (40 + srchExitButton.getPreferredSize().getWidth()));
            // search.setPreferredSize(sizes);
            // search.setBackground(new Color(88, 17, 17));
            // search.setForeground(new Color(218, 165, 32));
            // search.setFocusPainted(false);
            // search.setToolTipText("Search Yu-Gi-Oh! cards...");
            
            // srchPnl.add(search);
            // searchLayout.putConstraint(SpringLayout.WEST, search, (int)(32 + srchExitButton.getPreferredSize().getWidth()), SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, search, (int)(finalHorizSep.getPreferredSize().getHeight()), SpringLayout.NORTH, finalHorizSep);

            
            // searchLayout.putConstraint(SpringLayout.WEST, srchExitButton, 27, SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, srchExitButton, (int)(finalHorizSep.getPreferredSize().getHeight()), SpringLayout.NORTH, finalHorizSep);

            // srchPnl.add(finalHorizSep);
            // searchLayout.putConstraint(SpringLayout.WEST, finalHorizSep, 0, SpringLayout.WEST, srchPnl);
            // searchLayout.putConstraint(SpringLayout.NORTH, finalHorizSep, (int)(defEq.getPreferredSize().getHeight() + 5), SpringLayout.NORTH, defEq);
        } catch (IOException e) {
            // TODO: handle exception
        }

        //Make this program visible and set default close operation
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void fontLoading() {
        try {
            //Load each font from its respective .ttf file
            cardNameFont = Font.createFont(Font.TRUETYPE_FONT, new File("img/fonts/ygoCardNames.ttf"));
            cardEffectFont = Font.createFont(Font.TRUETYPE_FONT, new File("img/fonts/ygoEffects.ttf"));
            cardDescFont = Font.createFont(Font.TRUETYPE_FONT, new File("img/fonts/ygoNormMonsterDesc.ttf"));
            cardDetailsFont = Font.createFont(Font.TRUETYPE_FONT, new File("img/fonts/ygoAtkDefTypes.ttf"));
            cardSetFont = Font.createFont(Font.TRUETYPE_FONT, new File("img/fonts/setNames.ttf"));
            mainFont = Font.createFont(Font.TRUETYPE_FONT, new File("img/fonts/defaultFont.ttf"));
            titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("img/fonts/titleFont.ttf"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(contentPane, "Failed to load a font.", "Font Error", JOptionPane.ERROR_MESSAGE);
        } catch (FontFormatException e) {
            JOptionPane.showMessageDialog(contentPane, "Failed to load a font.", "Font Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fontRegistration() {
        //Register each loaded font with the GraphicsEnvironment
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(cardNameFont);
        ge.registerFont(cardEffectFont);
        ge.registerFont(cardDescFont);
        ge.registerFont(cardDetailsFont);
        ge.registerFont(cardSetFont);
        ge.registerFont(mainFont);
        ge.registerFont(titleFont);

        System.out.println(cardDescFont.getFamily());
    }

    private void createHomePanel() {
        try {
            //Attempt to load the image...
            homeImg = ImageIO.read(new File("img/system/homeBg.jpg"));
        } catch (IOException e) {
            //...it failed. Create a basic JPanel instead
            JOptionPane.showMessageDialog(contentPane, "Failed to load home screen background", "Loading Error", JOptionPane.ERROR_MESSAGE);
            homePnl = (ImagePanel)new JPanel();
            return;
        }
        //...it succeeded. Create the ImagePanel
        homePnl = new ImagePanel(homeImg);
    }

    private void createNewHomeLayout() {
        //Basic creation and setting of a layout
        homeLayout = new SpringLayout();
        homePnl.setLayout(homeLayout);
    }

    private void createLogoLabel() {
        //Load the logo and set it to a JLabel
        ygoLogo = new ImageIcon("img/system/ygoLogo.png");
        ygoSymbol = new JLabel(ygoLogo);
    }

    private void createTitleLabel() {
        //Using HTML, create a JLabel that contains the title of the database
        dbTitle = new JLabel("<html><head><style type=\"text/css\"> body { font-family: Sofachrome Rg; color: black; } </style></head><body><i>Database</i></body></html>");
        dbTitle.setFont(titleFont.deriveFont(36f));
    }

    private void createStartButton() {
        //Create a button that will open the database. Uses HTML to format the text
        startButton = new JButton("<html><head><style type=\"text/css\"> body { font-family: Sofachrome Rg; color: black; } </style></head><body>Start</body></html>");
        startButton.setFont(mainFont.deriveFont(28f));
        startButton.setBackground(Color.RED);
        startButton.setFocusPainted(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(contentPane, "This would normally start the application... For now though, it does nothing. Getting UI set up before running the logic.", "TEMP MESSAGE", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void createCreditsButton() {
        //Create a button that allows user to see information about the program
        creditsButton = new JButton("About", new ImageIcon("img/system/qMark.png"));
        //Make the button transparent
        creditsButton.setOpaque(false);
        creditsButton.setContentAreaFilled(false);
        creditsButton.setFocusPainted(false);
        creditsButton.setBorderPainted(false);
        //Ensure that text and image are drawn over each other
        creditsButton.setVerticalTextPosition(JButton.BOTTOM);
        creditsButton.setHorizontalTextPosition(JButton.CENTER);
        //Set color and size of font
        creditsButton.setForeground(Color.RED);
        creditsButton.setFont(mainFont.deriveFont(16f));
        //Add actionListener
        creditsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(contentPane, "<html>Developed by:<br>&emsp; Kamil Chyc-Teklarz<br>Some card art thanks to:<br>&emsp; D-Evil6661 (https://www.deviantart.com/d-evil6661)<br>&emsp; nhociory (https://www.deviantart.com/nhociory)<br>&emsp; Thong3 (https://www.deviantart.com/thong3)<br>Card information:<br>&emsp; YGOProdeck API (https://ygoprodeck.com/api-guide/)</html>", "Credits", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void createExitButton() {
        //Create a button that allows the user to close the program
        exitButton = new JButton("Exit", new ImageIcon("img/system/exitIcon.png"));
        //Make the button transparent
        exitButton.setOpaque(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        //Ensure that text and image are drawn over each other
        exitButton.setVerticalTextPosition(JButton.BOTTOM);
        exitButton.setHorizontalTextPosition(JButton.CENTER);
        //Set color and size of font
        exitButton.setForeground(Color.RED);
        exitButton.setFont(mainFont.deriveFont(16f));
        //Add actionListener
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int choice = JOptionPane.showConfirmDialog(contentPane, "Do you wish to close the program?", "Exit Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choice == JOptionPane.OK_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void setupUi() {
        //Logo location
        homeLayout.putConstraint(SpringLayout.WEST, ygoSymbol, (int)((homePnl.getWidth()/2) - (ygoSymbol.getPreferredSize().getWidth()/2)), SpringLayout.WEST, homePnl);
        homeLayout.putConstraint(SpringLayout.NORTH, ygoSymbol, 55, SpringLayout.NORTH, homePnl);
        //Title location
        homeLayout.putConstraint(SpringLayout.WEST, dbTitle, (int)((homePnl.getWidth()/2) - (dbTitle.getPreferredSize().getWidth()/2)), SpringLayout.WEST, homePnl);
        homeLayout.putConstraint(SpringLayout.NORTH, dbTitle, (int)(ygoSymbol.getPreferredSize().getHeight() + 70), SpringLayout.NORTH, homePnl);
        //Start button location
        homeLayout.putConstraint(SpringLayout.WEST, startButton, (int)((homePnl.getWidth()/2) - (startButton.getPreferredSize().getWidth()/2)), SpringLayout.WEST, homePnl);
        homeLayout.putConstraint(SpringLayout.NORTH, startButton, (int)(ygoSymbol.getPreferredSize().getHeight() + dbTitle.getPreferredSize().getHeight() + 100), SpringLayout.NORTH, homePnl);
        //Credits button location
        homeLayout.putConstraint(SpringLayout.WEST, creditsButton, (int)((homePnl.getWidth()) - (creditsButton.getPreferredSize().getWidth() + 27)), SpringLayout.WEST, homePnl);
        homeLayout.putConstraint(SpringLayout.NORTH, creditsButton, (int)((homePnl.getHeight()) - (creditsButton.getPreferredSize().getHeight() + 27)), SpringLayout.NORTH, homePnl);
        //Exit button location
        homeLayout.putConstraint(SpringLayout.WEST, exitButton, 27, SpringLayout.WEST, homePnl);
        homeLayout.putConstraint(SpringLayout.NORTH, exitButton, (int)((homePnl.getHeight()) - (creditsButton.getPreferredSize().getHeight() + 27)), SpringLayout.NORTH, homePnl);
    }

    private void buildUi() {
        //Simply adding all the components to the homePanel
        homePnl.add(ygoSymbol);
        homePnl.add(dbTitle);
        homePnl.add(startButton);
        homePnl.add(creditsButton);
        homePnl.add(exitButton);
    }
}