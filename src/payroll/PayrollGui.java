package payroll;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import exceptions.EmptyFieldException;
import exceptions.InputLengthExceededException;
import exceptions.InvalidLoginException;
import exceptions.IvalidEmailAddressException;
import exceptions.NoCompanyFoundException;
import exceptions.NoEmployeeFoundException;
import exceptions.NoUserFoundException;
import exceptions.WeekOutOfRangeException;
import exceptions.WrongDateFormatException;
import exceptions.WrongPpsFormatException;

//user interface class
public class PayrollGui extends JFrame implements ActionListener
{
    private JTabbedPane tabsPane;
    private JPanel comboBoxPanel,employeeDetails, timeSheet, reports, buttonPanel;
    private JComboBox surnameCbox, departmentCbox, prsiCbox;
    private JComboBox[] startHCbox, startMCbox, finishHCbox, finishMCbox; 
    
    private JTextField surnameTf, firstNameTf, dobTf, ppsTf, emailTf, addressLine1Tf, phoneTf, 
    					addressLine2Tf, addressLine3Tf, empNoTf, hourlyRateTf, weeklyPayTf, bankNameTf, 
    					sortCodeTf, accountNoTf, startDateTf, finishDateTf, 
    					cutOffTf, taxCreditTf,totEmpTf, tsEmpNameTf, tsWeekTf, tsYearTf,
    					tsExtraPayTf, searchTf;
    
    private JLabel selectLbl,totEmpLbl, surnameLbl, firstNameLbl, dobLbl, addressLbl, phoneLbl, emailLbl, 
    				departmentLbl, ppsLbl, empNoLbl, bankNameLbl, accountNoLbl, sortCodeLbl, payMethodLbl, 
                    startDateLbl, finishDateLbl, cutOffLbl, taxCreditLbl, prsiLbl,
                    tsEmpNameLbl, tsWeekLbl, tsExtrasLbl, searchLbl;
    
    private JTextArea reportTa, paySummaryTa;
    
    private JButton exitBt, addBt, updateBt, ceaseBt, nextBt, prevBt, tsSubmitBut, tsClearBut, tsCalcPayBut, 
    				tsNextWeekBut, tsPrevWeekBut, searchBut;
    
    private JRadioButton transferRb, cashRb, chequeRb, hourlyRateRb, weeklyPayRb, 
    						p30Rb, p35Rb, p60Rb, p45Rb, weeklyReportRb;

    private Font lblFont, summaryFont;
    private DbConn conn;
    private ResultSet rs;
    private ResultSet reportRs; //separate ResultSet to get reports from DB
    private ListComboBoxModel payrollModel;
    private String[] prsiClass = {"A", "B", "C", "D", "E"};
    private String[] departments = {"IT", "HR","ACC", "FIN"};
    String[] days = {"Monday", "Tuesday", "Wednday", "Thursday", "Friday", "Saturday", "Sunday"};
    String[] hours = {"00","1","2","3","4","5","6","7","8","9","10","11","12",
    					"13","14","15","16","17","18","19","20","21","22","23"};
    String[] minutes = {"00","15","30","45"};

    private ArrayList<String> surname;
    private int weekNo;
    private int year;
    private Employee emp;
    private RevenueDetails revDet;
    private BankDetails bankDet;
    private PayrollUser user;
    
    private Company company;
    private ImageIcon icon;
    private Image img;
    private JMenuBar mBar;
    private JMenu menu, menu2, menu3;
    private JMenuItem exitItem, addCompItem, delCompItem, helpItem;
    
    //constructor
    public PayrollGui()
    {
        super("Payroll"); //pass frame title to superclass
        setSize(900,670);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocation(200,50);
        
        weekNo=1;
        year=2012;
        conn = new DbConn();
        
        //Close database when exiting using the X button of the window
        addWindowListener( new WindowAdapter()
        {
        	public void windowClosing(WindowEvent we)
        	{
        		conn.disconnect();
        		System.exit(0);
        	}
        });
        
        //instantiates Panels to layout other components
        tabsPane = new JTabbedPane();
        employeeDetails = new JPanel();
        comboBoxPanel = new JPanel();
        timeSheet = new JPanel();
        reports = new JPanel();
        buttonPanel = new JPanel();
        
        tabsPane.add("Employee Details", employeeDetails);
        tabsPane.add("Time Sheet", timeSheet);
        tabsPane.add("Reports", reports);
        add(tabsPane);
        
        //Menu
        mBar = new JMenuBar();
        menu = new JMenu("System");
        menu2 = new JMenu("Company");
        menu3 = new JMenu("Help");
        
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this);
        
        addCompItem = new JMenuItem("Add company");
        addCompItem.addActionListener(this);
        delCompItem = new JMenuItem("Delete company");
        delCompItem.addActionListener(this);
        
        helpItem = new JMenuItem("User manual");
        helpItem.addActionListener(this);
        
        menu.add(exitItem);
        menu2.add(addCompItem);
        menu2.add(delCompItem);
        menu3.add(helpItem);
        mBar.add(menu);
        mBar.add(menu2);
        mBar.add(menu3);
        setJMenuBar(mBar);
        
        //sets Panels layouts and borders
        employeeDetails.setLayout(new GridLayout(2,0,0,0));
        JPanel personalDetailsTop = new JPanel(new GridLayout(0,2,20,20));
        personalDetailsTop.setBorder(BorderFactory.createTitledBorder(""));
        JPanel[] panel = new JPanel[7];
        for(int i=0; i<4; i++)
        {
        	panel[i] = new JPanel();
        	panel[i].setLayout(new BoxLayout(panel[i], BoxLayout.PAGE_AXIS));
        	panel[i].setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); 
        }
        JPanel personalDetailsBottom = new JPanel(new GridLayout(0,3,20,20));
        personalDetailsBottom.setBorder(BorderFactory.createTitledBorder(""));
        for(int i=4; i<7; i++)
        {
        	panel[i] = new JPanel();
        	panel[i].setLayout(new BoxLayout(panel[i], BoxLayout.PAGE_AXIS));
        	panel[i].setBorder(BorderFactory.createTitledBorder(""));//Integer.toString(i) - insert to display panel numbers
        	personalDetailsBottom.add(panel[i]);
        }
        employeeDetails.add(personalDetailsTop);
        employeeDetails.add(personalDetailsBottom);
        
       // timeSheet.setBorder(BorderFactory.createTitledBorder("TimeSheetPanel"));
        timeSheet.setLayout(new GridLayout(1,2,10,10));
        JPanel timeSheetLeft = new JPanel();
        JPanel timeSheetRight = new JPanel(new BorderLayout());
        timeSheetLeft.setBorder(BorderFactory.createTitledBorder(""));//TimeSheetLeft
        timeSheetRight.setBorder(BorderFactory.createTitledBorder(""));//TimeSheetRight
        timeSheetLeft.setLayout(new BorderLayout());
        timeSheet.add(timeSheetLeft);
        timeSheet.add(timeSheetRight);
        
        //buttons, labels, textfields, comboboxes, fonts and other smaller components etc.
        lblFont = new Font("arial", Font.BOLD, 14);
        summaryFont = new Font("arial", Font.BOLD, 14);
        
    	exitBt = new JButton("Exit");
    	exitBt.addActionListener(this);
		addBt = new JButton("Add New Employee...");
		addBt.addActionListener(this);
		updateBt = new JButton("Update Employee");
		updateBt.addActionListener(this);
		ceaseBt = new JButton("Cease Employment");
		ceaseBt.addActionListener(this);
		nextBt = new JButton("Next Employee>>");
		nextBt.addActionListener(this);
		prevBt = new JButton("<<Previous Employee");
		prevBt.addActionListener(this);
		transferRb = new JRadioButton("Transfer");
		transferRb.setSelected(true);
		transferRb.addActionListener(this);
		cashRb =  new JRadioButton("Cash");
		cashRb.addActionListener(this);
		chequeRb = new JRadioButton("Cheque");
		chequeRb.addActionListener(this);
		hourlyRateRb = new JRadioButton("Hourly rate:");
		hourlyRateRb.addActionListener(this);
		hourlyRateRb.setFont(lblFont);
		weeklyPayRb = new JRadioButton("Weekly pay:");
		weeklyPayRb.addActionListener(this);
		weeklyPayRb.setFont(lblFont);
		tsSubmitBut = new JButton("Submit");
		tsSubmitBut.addActionListener(this);
		tsClearBut = new JButton("Clear");
		tsClearBut.addActionListener(this);
		tsCalcPayBut = new JButton("Calculate");
		tsCalcPayBut.addActionListener(this);
		tsNextWeekBut = new JButton(" >>>");
		tsNextWeekBut.addActionListener(this);
		tsPrevWeekBut = new JButton("<<< ");
		tsPrevWeekBut.addActionListener(this);
		p30Rb = new JRadioButton("P30");
		p30Rb.addActionListener(this);
		p35Rb = new JRadioButton("P35");
		p35Rb.addActionListener(this);
		p60Rb = new JRadioButton("P60");
		p60Rb.addActionListener(this);
		p45Rb = new JRadioButton("P45");
		p45Rb.addActionListener(this);
		weeklyReportRb = new JRadioButton("Weekly report");
		weeklyReportRb.addActionListener(this);
		searchBut = new JButton("Search");
		searchBut.addActionListener(this);
		
		//Weekly Report Radio buttons (P30, P35, P45 etc)
		ButtonGroup reportGroup = new ButtonGroup();
		reportGroup.add(p30Rb);
		reportGroup.add(p35Rb);
		reportGroup.add(p60Rb);
		reportGroup.add(p45Rb);
		reportGroup.add(weeklyReportRb);
		
		//Radio buttons for payment method on employee tab
		ButtonGroup bankGroup = new ButtonGroup();
		bankGroup.add(cashRb);
		bankGroup.add(chequeRb);
		bankGroup.add(transferRb);
		
		//Radio buttons for employee type on the employee tab
		ButtonGroup payGroup = new ButtonGroup();
		payGroup.add(hourlyRateRb);
		payGroup.add(weeklyPayRb);
		
		//Employee Tab labels
		selectLbl = new JLabel("Select employee:");
		totEmpLbl = new JLabel("Total number of employees:");
        surnameLbl = new JLabel("Surname:");
        surnameLbl.setFont(lblFont);
        firstNameLbl = new JLabel("First name:");
        firstNameLbl.setFont(lblFont);
        dobLbl = new JLabel("Date of birth:");
        dobLbl.setFont(lblFont);
        addressLbl = new JLabel("Address:");
        addressLbl.setFont(lblFont);
        phoneLbl = new JLabel("Phone number:");
        phoneLbl.setFont(lblFont);
        emailLbl = new JLabel("E-mail address:"); 
        emailLbl.setFont(lblFont);
        departmentLbl = new JLabel("Department:");
        departmentLbl.setFont(lblFont);
        ppsLbl = new JLabel("PPS number:");
        ppsLbl.setFont(lblFont);
        empNoLbl = new JLabel("Employee number:");
        empNoLbl.setFont(lblFont);
        bankNameLbl = new JLabel("Bank name:");
        accountNoLbl = new JLabel("Account number:");
        sortCodeLbl = new JLabel("Sort code:");
        payMethodLbl = new JLabel("Payment method:");
        startDateLbl = new JLabel("Start date:");
        finishDateLbl = new JLabel("Finish date:");
        cutOffLbl = new JLabel("Cutoff point:");
        taxCreditLbl = new JLabel("Tax credit:");
        prsiLbl = new JLabel("PRSI:");
        tsEmpNameLbl = new JLabel("Employee name:");
        tsWeekLbl = new JLabel("Week no:");
        tsExtrasLbl = new JLabel("Extras");
        searchLbl = new JLabel("Search emloyee by name:");
           
        //Employee tab Textfields
        firstNameTf = new JTextField(15);
        firstNameTf.setBorder(BorderFactory.createLoweredBevelBorder());
        surnameTf = new JTextField(20);
        surnameTf.setBorder(BorderFactory.createLoweredBevelBorder());
        dobTf = new JTextField(8);
        dobTf.setBorder(BorderFactory.createLoweredBevelBorder());
        ppsTf = new JTextField(9);
        ppsTf.setBorder(BorderFactory.createLoweredBevelBorder());
        emailTf = new JTextField(20);
        emailTf.setBorder(BorderFactory.createLoweredBevelBorder());
        phoneTf = new JTextField(10);
        phoneTf.setBorder(BorderFactory.createLoweredBevelBorder());
        addressLine1Tf = new JTextField(20);
        addressLine1Tf.setBorder(BorderFactory.createLoweredBevelBorder());
        addressLine2Tf = new JTextField(20);
        addressLine2Tf.setBorder(BorderFactory.createLoweredBevelBorder());
        addressLine3Tf = new JTextField(20);
        addressLine3Tf.setBorder(BorderFactory.createLoweredBevelBorder());
        empNoTf = new JTextField(5);
        empNoTf.setBorder(BorderFactory.createLoweredBevelBorder());
        empNoTf.setEditable(false);
        hourlyRateTf = new JTextField(5);
        hourlyRateTf.setBorder(BorderFactory.createLoweredBevelBorder());
        weeklyPayTf = new JTextField(7);
        weeklyPayTf.setBorder(BorderFactory.createLoweredBevelBorder());
        bankNameTf = new JTextField(10);
        bankNameTf.setBorder(BorderFactory.createLoweredBevelBorder());
        sortCodeTf = new JTextField(20);
        sortCodeTf.setBorder(BorderFactory.createLoweredBevelBorder());
        accountNoTf = new JTextField(20);
        accountNoTf.setBorder(BorderFactory.createLoweredBevelBorder());
        startDateTf = new JTextField(10);
        startDateTf.setBorder(BorderFactory.createLoweredBevelBorder());
        finishDateTf = new JTextField(10);
        finishDateTf.setBorder(BorderFactory.createLoweredBevelBorder());
        finishDateTf.setEditable(false);
        cutOffTf = new JTextField(10);
        cutOffTf.setBorder(BorderFactory.createLoweredBevelBorder());
        taxCreditTf  = new JTextField(10);
        taxCreditTf.setBorder(BorderFactory.createLoweredBevelBorder());
        totEmpTf = new JTextField(3);
        totEmpTf.setBorder(BorderFactory.createLoweredBevelBorder());
        totEmpTf.setEditable(false);
        tsEmpNameTf = new JTextField(20);
        tsEmpNameTf.setBorder(BorderFactory.createLoweredBevelBorder());
        tsEmpNameTf.setEditable(false);
        tsWeekTf = new JTextField(3);
        tsWeekTf.setBorder(BorderFactory.createLoweredBevelBorder());
        tsWeekTf.setEditable(false);
        tsYearTf = new JTextField(3);
        tsYearTf.setBorder(BorderFactory.createLoweredBevelBorder());
        tsExtraPayTf = new JTextField(10);
        tsExtraPayTf.setText("0.00");
        tsExtraPayTf.setBorder(BorderFactory.createLoweredBevelBorder());
        reportTa = new JTextArea();
        reportTa.setFont(summaryFont);   
        searchTf = new JTextField(15);
        searchTf.setBorder(BorderFactory.createLoweredBevelBorder());
        //surnameList = new String[dbConn.TOT_EMPLOYEE];
     
        surname = new ArrayList<String>(); // ArrayList is resizable as opposed to simple array
        payrollModel = new ListComboBoxModel(surname);
		prsiCbox = new JComboBox(prsiClass);
		prsiCbox.setEditable(false);
        surnameCbox = new JComboBox(); 
        surnameCbox.setModel(payrollModel); 
        surnameCbox.addActionListener(this);
        surnameCbox.setEditable(false);
        comboBoxPanel.setLayout(new GridLayout(2,1));
        JPanel comboBoxPanelTop = new JPanel();
        comboBoxPanelTop.add(selectLbl);
        comboBoxPanelTop.add(surnameCbox);
        comboBoxPanelTop.add(totEmpLbl);
        comboBoxPanelTop.add(totEmpTf);
        JPanel comboBoxPanelBottom = new JPanel();
        comboBoxPanelBottom.add(searchLbl);
        comboBoxPanelBottom.add(searchTf);
        comboBoxPanelBottom.add(searchBut);
        comboBoxPanel.add(comboBoxPanelTop);
        comboBoxPanel.add(comboBoxPanelBottom);
        
        departmentCbox = new JComboBox(departments);
        departmentCbox.setEditable(false);
        
        //Combo Boxes for timesheet
        startHCbox = new JComboBox[7];
	    startMCbox = new JComboBox[7];
	    finishHCbox = new JComboBox[7];
	    finishMCbox = new JComboBox[7];
       
        //add all components to panels 
        buttonPanel.add(prevBt);
        buttonPanel.add(nextBt);
        buttonPanel.add(addBt);
        buttonPanel.add(updateBt);
        buttonPanel.add(ceaseBt);
        buttonPanel.add(exitBt);
        add(buttonPanel, BorderLayout.SOUTH);
        add(comboBoxPanel, BorderLayout.NORTH);
        
        panel[0].add(surnameLbl);
        surnameLbl.setAlignmentX(RIGHT_ALIGNMENT);
		panel[0].add(Box.createGlue());
        panel[0].add(firstNameLbl);
        firstNameLbl.setAlignmentX(RIGHT_ALIGNMENT);
		panel[0].add(Box.createGlue());
		panel[0].add(addressLbl);
		addressLbl.setAlignmentX(RIGHT_ALIGNMENT);
		panel[0].add(Box.createGlue());
		panel[0].add(Box.createGlue());
		panel[0].add(Box.createGlue());
        panel[0].add(phoneLbl);
        phoneLbl.setAlignmentX(RIGHT_ALIGNMENT);
        panel[0].add(Box.createGlue());
        panel[0].add(emailLbl);
        emailLbl.setAlignmentX(RIGHT_ALIGNMENT);
		
        panel[1].add(surnameTf);
		surnameTf.setMaximumSize(surnameTf.getPreferredSize());
		surnameTf.setAlignmentX(LEFT_ALIGNMENT);
		panel[1].add(Box.createGlue());
		panel[1].add(firstNameTf);
		firstNameTf.setMaximumSize(firstNameTf.getPreferredSize());
		firstNameTf.setAlignmentX(LEFT_ALIGNMENT);
		panel[1].add(Box.createGlue());
		panel[1].add(addressLine1Tf);
		addressLine1Tf.setMaximumSize(addressLine1Tf.getPreferredSize());
		addressLine1Tf.setAlignmentX(LEFT_ALIGNMENT);
		panel[1].add(addressLine2Tf);
		addressLine2Tf.setMaximumSize(addressLine2Tf.getPreferredSize());
		addressLine2Tf.setAlignmentX(LEFT_ALIGNMENT);
		panel[1].add(addressLine3Tf);
		addressLine3Tf.setMaximumSize(addressLine3Tf.getPreferredSize());
		addressLine3Tf.setAlignmentX(LEFT_ALIGNMENT);
		panel[1].add(Box.createGlue());
		panel[1].add(phoneTf);
		phoneTf.setMaximumSize(phoneTf.getPreferredSize());
		phoneTf.setAlignmentX(LEFT_ALIGNMENT);
		panel[1].add(Box.createGlue());
		panel[1].add(emailTf);
		emailTf.setMaximumSize(emailTf.getPreferredSize());
		emailTf.setAlignmentX(LEFT_ALIGNMENT);
		JPanel empDetPanel1 = new JPanel();
		empDetPanel1.setBorder(BorderFactory.createTitledBorder(""));
		empDetPanel1.setLayout(new BoxLayout(empDetPanel1, BoxLayout.LINE_AXIS));
		empDetPanel1.add(panel[0]);
		empDetPanel1.add(panel[1]);
		personalDetailsTop.add(empDetPanel1);
		
		panel[2].add(dobLbl);
	    dobLbl.setAlignmentX(RIGHT_ALIGNMENT);
	    panel[2].add(Box.createGlue());
	    panel[2].add(Box.createGlue());
	    panel[2].add(Box.createGlue());
	    panel[2].add(Box.createGlue());
	    panel[2].add(ppsLbl);
	    ppsLbl.setAlignmentX(RIGHT_ALIGNMENT);
	    panel[2].add(Box.createGlue());
	    panel[2].add(Box.createGlue());
	    panel[2].add(Box.createGlue());
	    panel[2].add(Box.createGlue());
		panel[2].add(empNoLbl);
		empNoLbl.setAlignmentX(RIGHT_ALIGNMENT);
		panel[2].add(Box.createGlue());
		panel[2].add(Box.createGlue());
		panel[2].add(Box.createGlue());
		panel[2].add(Box.createGlue());
		panel[2].add(departmentLbl);
		departmentLbl.setAlignmentX(RIGHT_ALIGNMENT);
		panel[2].add(Box.createGlue());
		panel[2].add(Box.createGlue());
		panel[2].add(Box.createGlue());
		panel[2].add(Box.createGlue());
		panel[2].add(hourlyRateRb);
		hourlyRateRb.setAlignmentX(RIGHT_ALIGNMENT);
		panel[2].add(Box.createGlue());
		panel[2].add(Box.createGlue());
		panel[2].add(Box.createGlue());
		panel[2].add(weeklyPayRb);
		weeklyPayRb.setAlignmentX(RIGHT_ALIGNMENT);
		
		panel[3].add(dobTf);
		dobTf.setMaximumSize(dobTf.getPreferredSize());
		dobTf.setAlignmentX(LEFT_ALIGNMENT);
		panel[3].add(Box.createGlue());
		panel[3].add(ppsTf);
		ppsTf.setMaximumSize(ppsTf.getPreferredSize());
		ppsTf.setAlignmentX(LEFT_ALIGNMENT);
		panel[3].add(Box.createGlue());
		panel[3].add(empNoTf);
		empNoTf.setMaximumSize(empNoTf.getPreferredSize());
		empNoTf.setAlignmentX(LEFT_ALIGNMENT);
		panel[3].add(Box.createGlue());
		panel[3].add(departmentCbox);
		departmentCbox.setAlignmentX(LEFT_ALIGNMENT);
		departmentCbox.setMaximumSize(new Dimension(80,25));
		panel[3].add(Box.createGlue());
		panel[3].add(hourlyRateTf);
		hourlyRateTf.setMaximumSize(hourlyRateTf.getPreferredSize());
		hourlyRateTf.setAlignmentX(LEFT_ALIGNMENT);
		panel[3].add(Box.createGlue());
		panel[3].add(weeklyPayTf);
		weeklyPayTf.setMaximumSize(weeklyPayTf.getPreferredSize());
		weeklyPayTf.setAlignmentX(LEFT_ALIGNMENT);
		JPanel empDetPanel2 = new JPanel();
		empDetPanel2.setBorder(BorderFactory.createTitledBorder(""));
		empDetPanel2.setLayout(new BoxLayout(empDetPanel2, BoxLayout.LINE_AXIS));
		empDetPanel2.add(panel[2]);
		empDetPanel2.add(panel[3]);
		personalDetailsTop.add(empDetPanel2);
		
		panel[4].setLayout(new GridLayout(0,2,10,0));
		JPanel empLblPanel = new JPanel();
		empLblPanel.setLayout(new BoxLayout(empLblPanel, BoxLayout.PAGE_AXIS));
		empLblPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 0, 5));//empLblPanel
		empLblPanel.add(startDateLbl);
		startDateLbl.setAlignmentX(RIGHT_ALIGNMENT);
		empLblPanel.add(Box.createVerticalStrut(15));
		empLblPanel.add(finishDateLbl);
		finishDateLbl.setAlignmentX(RIGHT_ALIGNMENT);
		empLblPanel.add(Box.createVerticalStrut(10));
		JPanel empTfPanel = new JPanel();
		empTfPanel.setLayout(new BoxLayout(empTfPanel, BoxLayout.PAGE_AXIS));
		empTfPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 0, 5));//empTfPanel
		empTfPanel.add(startDateTf);
		startDateTf.setMaximumSize(startDateTf.getPreferredSize());
		startDateTf.setAlignmentX(LEFT_ALIGNMENT);
		empTfPanel.add(Box.createVerticalStrut(10));
		empTfPanel.add(finishDateTf);
		finishDateTf.setMaximumSize(finishDateTf.getPreferredSize());
		finishDateTf.setAlignmentX(LEFT_ALIGNMENT);
		empTfPanel.add(Box.createVerticalStrut(10));
		panel[4].add(empLblPanel);
		panel[4].add(empTfPanel);
		
		panel[5].setLayout(new GridLayout(0,2,10,0));
		JPanel accLblPanel = new JPanel();
		accLblPanel.setLayout(new BoxLayout(accLblPanel, BoxLayout.PAGE_AXIS));
		accLblPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 0, 5));//accLblPanel
		accLblPanel.add(payMethodLbl);
		payMethodLbl.setAlignmentX(RIGHT_ALIGNMENT);
		accLblPanel.add(Box.createVerticalStrut(58));
		accLblPanel.add(bankNameLbl);
		bankNameLbl.setAlignmentX(RIGHT_ALIGNMENT);
		accLblPanel.add(Box.createVerticalStrut(10));
		accLblPanel.add(accountNoLbl);
		accountNoLbl.setAlignmentX(RIGHT_ALIGNMENT);
		accLblPanel.add(Box.createVerticalStrut(10));
		accLblPanel.add(sortCodeLbl);
		sortCodeLbl.setAlignmentX(RIGHT_ALIGNMENT);
		
		
		JPanel accTfPanel = new JPanel();
		JPanel rbPanel = new JPanel();
		rbPanel.setLayout(new BoxLayout(rbPanel, BoxLayout.Y_AXIS));
		//rbPanel.setBorder(BorderFactory.createTitledBorder(""));//rbPanel
		rbPanel.add(transferRb);
		rbPanel.add(cashRb);
		rbPanel.add(chequeRb);
		accTfPanel.setLayout(new BoxLayout(accTfPanel, BoxLayout.PAGE_AXIS));
		accTfPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 0, 5));//accTfPanel
		accTfPanel.add(rbPanel);
		accTfPanel.add(Box.createVerticalStrut(20));
		rbPanel.setAlignmentX(LEFT_ALIGNMENT);
		accTfPanel.add(bankNameTf);
		accTfPanel.add(Box.createVerticalStrut(5));
		bankNameTf.setAlignmentX(LEFT_ALIGNMENT);
		bankNameTf.setMaximumSize(bankNameTf.getPreferredSize());
		accTfPanel.add(accountNoTf);
		accountNoTf.setAlignmentX(LEFT_ALIGNMENT);
		accountNoTf.setMaximumSize(accountNoTf.getPreferredSize());
		accTfPanel.add(Box.createVerticalStrut(5));
		accTfPanel.add(sortCodeTf);
		sortCodeTf.setAlignmentX(LEFT_ALIGNMENT);
		sortCodeTf.setMaximumSize(sortCodeTf.getPreferredSize());
		panel[5].add(accLblPanel);
		panel[5].add(accTfPanel);
		
		panel[6].setLayout(new GridLayout(0,2,10,0));
		JPanel taxLblPanel = new JPanel();
		taxLblPanel.setLayout(new BoxLayout(taxLblPanel, BoxLayout.PAGE_AXIS));
		taxLblPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 0, 5));//taxLblPanel
		taxLblPanel.add(Box.createVerticalStrut(5));
		taxLblPanel.add(prsiLbl);
		prsiLbl.setAlignmentX(RIGHT_ALIGNMENT);
		taxLblPanel.add(Box.createVerticalStrut(15));
		taxLblPanel.add(cutOffLbl);
		cutOffLbl.setAlignmentX(RIGHT_ALIGNMENT);
		taxLblPanel.add(Box.createVerticalStrut(15));
		taxLblPanel.add(taxCreditLbl);
		taxCreditLbl.setAlignmentX(RIGHT_ALIGNMENT);
		JPanel taxTfPanel = new JPanel();
		taxTfPanel.setLayout(new BoxLayout(taxTfPanel, BoxLayout.PAGE_AXIS));
		taxTfPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 0, 5));//taxTfPanel
		taxTfPanel.add(prsiCbox);
		prsiCbox.setAlignmentX(LEFT_ALIGNMENT);
		prsiCbox.setMaximumSize(new Dimension(80,25));
		taxTfPanel.add(Box.createVerticalStrut(10));
		taxTfPanel.add(cutOffTf);
		cutOffTf.setAlignmentX(LEFT_ALIGNMENT);
		cutOffTf.setMaximumSize(cutOffTf.getPreferredSize());
		taxTfPanel.add(Box.createVerticalStrut(10));
		taxTfPanel.add(taxCreditTf);
		taxCreditTf.setAlignmentX(LEFT_ALIGNMENT);
		taxCreditTf.setMaximumSize(taxCreditTf.getPreferredSize());
		panel[6].add(taxLblPanel);
		panel[6].add(taxTfPanel);
		
		//TimeSheet Panel
		JPanel tsEmp = new JPanel();
	    tsEmp.add(tsEmpNameLbl);
	    tsEmp.add(tsEmpNameTf);
	    JPanel tsWeekPanel = new JPanel();
	    tsWeekPanel.add(tsPrevWeekBut);
	    tsWeekPanel.add(tsWeekLbl);
	    tsWeekPanel.add(tsWeekTf);
	    tsWeekPanel.add(tsNextWeekBut);
	    tsWeekPanel.add(new JLabel("Year "));
	    tsWeekPanel.add(tsYearTf);
	    JPanel tsDetailsPanel = new JPanel(new GridLayout(2,1));
	    tsDetailsPanel.add(tsEmp);
	    tsDetailsPanel.add(tsWeekPanel);
	    timeSheetLeft.add(tsDetailsPanel, BorderLayout.NORTH);
	    
	    JPanel tsDaysPanel = new JPanel();
	    tsDaysPanel.setLayout(new GridLayout(9,1,0,6));
	    tsDaysPanel.setBorder(BorderFactory.createTitledBorder(""));//tsDaysPanel
	    timeSheetLeft.add(tsDaysPanel, BorderLayout.CENTER);
	    JPanel tsButtonPanel = new JPanel();
	    tsButtonPanel.add(tsCalcPayBut);
	    tsButtonPanel.add(tsSubmitBut);
	    tsButtonPanel.add(tsClearBut);
	    timeSheetLeft.add(tsButtonPanel, BorderLayout.SOUTH);
	    JPanel tsLblPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    JLabel tsStartLbl = new JLabel("Start time");
	    JLabel tsFinishLbl = new JLabel("Finish time");
	    
	    tsLblPanel.add(tsStartLbl);
	    tsLblPanel.add(new JLabel("                    "));
	    tsLblPanel.add(tsFinishLbl);
	    tsLblPanel.add(new JLabel("         "));
	    tsDaysPanel.add(tsLblPanel);
	    
	    JLabel[] daysLbl = new JLabel[7];
	    JPanel[] daysPanel = new JPanel[7];
	    JPanel[] tsTimePanel = new JPanel[7];
	    
	    for(int i =0; i<tsTimePanel.length; i++)
	    {
	    	tsTimePanel[i] = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    	tsTimePanel[i].setLayout(new BoxLayout(tsTimePanel[i], BoxLayout.LINE_AXIS));
	    	tsTimePanel[i].setBackground(new Color(250,250,250));
	    	tsTimePanel[i].setBorder(BorderFactory.createEtchedBorder());
	    	startHCbox[i] = new JComboBox(hours);
	    	startMCbox[i] = new JComboBox(minutes);
	    	finishHCbox[i] = new JComboBox(hours);
	    	finishMCbox[i] = new JComboBox(minutes);
	    	daysLbl[i] = new JLabel(days[i]);
	    	daysPanel[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    	daysPanel[i].setPreferredSize(new Dimension(80,0));
	    	daysPanel[i].setBackground(new Color(250,250,250));
	    	daysPanel[i].add(daysLbl[i]);
	    	tsTimePanel[i].add(daysPanel[i]);
	    	tsTimePanel[i].add(startHCbox[i]);
	    	startHCbox[i].setBorder(BorderFactory.createLoweredBevelBorder());
	    	startHCbox[i].setAlignmentX(RIGHT_ALIGNMENT);
	    	tsTimePanel[i].add(new JLabel("  :  "));
	    	tsTimePanel[i].add(startMCbox[i]);
	    	startMCbox[i].setBorder(BorderFactory.createLoweredBevelBorder());
	    	startMCbox[i].setAlignmentX(RIGHT_ALIGNMENT);
	    	tsTimePanel[i].add(new JLabel("    -    "));
	    	tsTimePanel[i].add(finishHCbox[i]);
	    	finishHCbox[i].setBorder(BorderFactory.createLoweredBevelBorder());
	    	finishHCbox[i].setAlignmentX(RIGHT_ALIGNMENT);
	    	tsTimePanel[i].add(new JLabel("  :  "));
	    	tsTimePanel[i].add(finishMCbox[i]);
	    	finishMCbox[i].setBorder(BorderFactory.createLoweredBevelBorder());
	    	finishMCbox[i].setAlignmentX(RIGHT_ALIGNMENT);
	    	tsTimePanel[i].add(new JLabel("  "));
	    	tsDaysPanel.add(tsTimePanel[i]);
	    }
	    JPanel extraPayPanel = new JPanel();
	    extraPayPanel.setLayout(new BoxLayout(extraPayPanel, BoxLayout.LINE_AXIS));
	    extraPayPanel.setBorder(BorderFactory.createEtchedBorder());
	    extraPayPanel.add(tsExtrasLbl);
	    extraPayPanel.add(Box.createGlue());
	    extraPayPanel.add(tsExtraPayTf);
	    tsExtraPayTf.setAlignmentY(CENTER_ALIGNMENT);
	    tsExtraPayTf.setMaximumSize(tsExtraPayTf.getPreferredSize());
	    tsDaysPanel.add(extraPayPanel);
	    
	    paySummaryTa = new JTextArea();
	    paySummaryTa.setFont(summaryFont);
	    paySummaryTa.setBorder(BorderFactory.createLoweredBevelBorder());
	    paySummaryTa.setSize(new Dimension(100,100));
	    paySummaryTa.setEditable(false);
	    paySummaryTa.setBackground(new Color(255,255,240));
	    JScrollPane sumPanel = new JScrollPane(paySummaryTa);
	    timeSheetRight.add(sumPanel);
	    
	    //reports Panel
	    reports.setLayout(new BorderLayout());
	    JPanel reportsLeftPanel = new JPanel();
	    reportsLeftPanel.setBorder(BorderFactory.createTitledBorder(""));//Report Left panel
	    reportsLeftPanel.setLayout(new BoxLayout(reportsLeftPanel, BoxLayout.PAGE_AXIS));
	    JPanel reportsRightPanel = new JPanel(new BorderLayout());
	    reportsRightPanel.setBorder(BorderFactory.createTitledBorder(""));//Report Right panel
	    
	    ImageIcon  i = new ImageIcon(getClass().getResource("/icon2.png"));
	    Image img2 = i.getImage();
	    ImagePanel reportImgPanel = new ImagePanel(img2,0,20,250,250);
	    
	    JPanel reportsBottomPanel = new JPanel(new BorderLayout()); 
	    reportsBottomPanel.add(reportImgPanel, BorderLayout.CENTER);
	    
	    JPanel reportsTopPanel = new JPanel(); 
	    reportsTopPanel.setBorder(BorderFactory.createTitledBorder(""));//Report Top panel
	    reportsTopPanel.setPreferredSize(new Dimension(250,100));
	    reports.add(reportsLeftPanel, BorderLayout.WEST);
	    reports.add(reportsRightPanel, BorderLayout.CENTER);
	    reportTa.setEditable(false);
	    reportTa.setBackground(new Color(255,255,240));
	    
	    JScrollPane reportScrollPanel = new JScrollPane(reportTa);
	    reportsTopPanel.add(p30Rb);
	    reportsTopPanel.add(p35Rb);
	    reportsTopPanel.add(p60Rb);
	    reportsTopPanel.add(p45Rb);
	    reportsTopPanel.add(weeklyReportRb);
	    reportsLeftPanel.add(reportsTopPanel);
	    reportsLeftPanel.add(reportsBottomPanel);
	    reportsRightPanel.add(reportScrollPanel);
	    	    
    	startPayroll();
    }//end constructor
    
	public void actionPerformed(ActionEvent ae)
	{
		Object src = ae.getSource();
		
		//Surname combo box to search employees
		if(src == surnameCbox)
		{
			int index = surnameCbox.getSelectedIndex()+1;
				try
				{
					if(surnameCbox.getSelectedIndex() == 0 
						|| surnameCbox.getSelectedIndex() == -1)
					{
						index = 1;
					}
					rs.absolute(index);
					this.emp = getEmployee(rs);
					displayEmpDetails(this.emp);
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
		}
		//Search employees by search field
		else if(src == searchBut)
		{
			searchEmployee(searchTf.getText());
			
		}
		//Exit the program, asks user to confirm their choice
		else if(src == exitBt)		
		{
			if(user != null )
			{
				int option = JOptionPane.showConfirmDialog(null,"Are you sure, " + this.user.getFName() +" ?", 
								"SW Payroll",JOptionPane.YES_NO_OPTION);
				if(option == JOptionPane.YES_OPTION)
				{
					conn.disconnect();
					System.exit(0);
				}
			}
			else
			{
				conn.disconnect();
				System.exit(0);
			}
		}
		//Scroll to next employee in resultset
		else if(src == nextBt)
		{
			try
			{
				if(rs.next())
				{
					this.emp = getEmployee(rs);
					displayEmpDetails(this.emp);
				}
				else
				{
					rs.previous();
					throw new NoEmployeeFoundException();
				}
			}
			catch(NoEmployeeFoundException nefe)
			{
				JOptionPane.showMessageDialog(null, nefe.getMessage());
			}
			catch(SQLException e)
			{
				
			}
		}
		//Scroll to previous employee in the resultset
		else if(src == prevBt)
		{
			try
			{
				if(rs.previous())
				{
					this.emp = getEmployee(rs);
					displayEmpDetails(this.emp);
				}
				else
				{
					rs.next();
					throw new NoEmployeeFoundException();
				}
			}
			catch(NoEmployeeFoundException nefe)
			{
				JOptionPane.showMessageDialog(null, nefe.getMessage());
			}
			catch(SQLException e)
			{
			
			}
		}
		//Update existing employee details
		else if(src == updateBt)
		{
			updateEmpDetails(this.emp);	
		}//Add new employee to database
		else if(src == addBt)
		{	
			addEmployee();
		}
		//Cease current employees employment
		else if(src == ceaseBt)
		{
			ceaseEmployment(this.emp);
		}
		//Grey out unselected radio button (Salary/Hour rate)
		else if(src == hourlyRateRb)
		{
			weeklyPayTf.setEditable(false);
			hourlyRateTf.setEditable(true);
		}
		//Grey out unselected radio button (Salary/Hour rate)
		else if(src == weeklyPayRb)
		{
			hourlyRateTf.setEditable(false);
			weeklyPayTf.setEditable(true);
		}
		//Grey out unselected radio button (Payment Method)
		else if(src == cashRb || ae.getSource() == chequeRb)
		{
			bankNameTf.setEditable(false);
			sortCodeTf.setEditable(false);
			accountNoTf.setEditable(false);
		}
		//Grey out unselected radio button (Payment Method)
		else if(src == transferRb)
		{
			bankNameTf.setEditable(true);
			sortCodeTf.setEditable(true);
			accountNoTf.setEditable(true);
		}
		//Clear selection
		else if(src == tsClearBut)
		{
			for(int i=0; i<7; i++)
			{
				startHCbox[i].setSelectedIndex(0);
				startMCbox[i].setSelectedIndex(0);
				finishHCbox[i].setSelectedIndex(0);
				finishMCbox[i].setSelectedIndex(0);
			}
		}
		//Calculate pay, creating a timesheet and WeeklyPay employee.
		else if(src == tsCalcPayBut)
		{
			createTimesheet(this.emp);
			createWeeklyPay(this.emp);
		}
		//Commit changes to the database
		else if(src == tsSubmitBut)
		{	
			conn.insertTimesheet(createTimesheet(this.emp));
			WeeklyPay wp = createWeeklyPay(this.emp);
			conn.insertWeeklyPay(wp);
			conn.insertTotalPay(wp);
		}
		//Advances displayed week by one week
		else if(src == tsNextWeekBut)
		{
			tsWeekTf.setText(String.valueOf(++this.weekNo));
			displayTimeSheet(this.emp,this.weekNo, this.year);
		}
		//Regresses displayed week by one week
		else if(src == tsPrevWeekBut)
		{
			tsWeekTf.setText(String.valueOf(--this.weekNo));
			displayTimeSheet(this.emp,this.weekNo, this.year);
		}
		//Exit and close connection
		else if(src == exitItem)
		{
			if(user != null )
			{
				int option = JOptionPane.showConfirmDialog(null,"Are you sure, " + this.user.getFName() +" ?", 
								"SW Payroll",JOptionPane.YES_NO_OPTION);
				if(option == JOptionPane.YES_OPTION)
				{
					conn.disconnect();
					System.exit(0);
				}
			}
			else
			{
				conn.disconnect();
				System.exit(0);
			}
		}
		else if(src == addCompItem)
		{
			createCompany();
		}
		else if(src == delCompItem)
		{
			deleteCompany();
		}
		//open up online User Manual from Help menu - Internet access required
		else if(src == helpItem)
		{
			try
			{ 
				Runtime.getRuntime().exec("cmd /c start http://www.smur89.com"); 
			} 
			catch(IOException e) 
			{
				e.printStackTrace();
			} 	
		}
		//Show P30 if p30 RadioButton Selected
		else if(src == p30Rb)
		{
			showP30();
		}
		//Show P35 if p35 RadioButton Selected
		else if(src == p35Rb)
		{
			showP35();
		}
		//Show P60 if p60 RadioButton Selected
		else if(src == p60Rb)
		{
			showP60();
		}
		//Show P45 if p45 RadioButton Selected
		else if(src == p45Rb)
		{
			showP45();
		}
		//Show Weekly Report if Weekly Report RadioButton Selected
		else if(src == weeklyReportRb)
		{
			showWeeklyReport();
		}
	}
	
	//Search employee by name
	private void searchEmployee(String name)
	{
		try
		{
			//New JPanel to allow user to enter name to search for.
			rs = conn.getSearchEmp(name);
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.add(new JLabel("Select employee:"));
			JRadioButton[] empRb;
			ButtonGroup group = new ButtonGroup();
			int size =0;
			if(rs != null && rs.next())
			{
				rs.last();
				size = rs.getRow();
				rs.beforeFirst();
				empRb = new JRadioButton[size]; //RadioButton for all matches returned
				
				for(int i=0; i<size; i++)
				{
					rs.next();
					empRb[i] = new JRadioButton(rs.getString("emp_fname") + " " + rs.getString("emp_surname"));
					group.add(empRb[i]);
					panel.add(empRb[i]);
				}
				
				int option = JOptionPane.showConfirmDialog(null, panel, "Matches found", JOptionPane.OK_CANCEL_OPTION);
				
				//Display details of the employee in the selected radiobutton
				if(option == JOptionPane.OK_OPTION)
				{
					for(int i=0; i<size; i++)
					{
						if(empRb[i].isSelected())
						{
							rs.absolute(i+1);
							this.emp = getEmployee(rs);
							displayEmpDetails(this.emp);
							searchTf.setText("");
						}
					}
					rs = conn.getAllEmployees();
				}
				//If user cancels search, return display first employee in the databse
				else
				{
					rs = conn.getAllEmployees();
					rs.next();
					this.emp = getEmployee(rs);
					displayEmpDetails(this.emp);
					searchTf.setText("");
				}
			}
			//If no employees found, display first employee in the database and throw exception informing user.
			else
			{
				rs = conn.getAllEmployees();
				rs.next();
				this.emp = getEmployee(rs);
				displayEmpDetails(this.emp);
				searchTf.setText("");
				throw new NoEmployeeFoundException();
			}
		}
		catch(NoEmployeeFoundException nefe)
		{
			JOptionPane.showMessageDialog(null, nefe.getMessage());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	//Start payroll system. Gets all data required to begin using the program
	public void startPayroll()
	{
		try
		{
			rs = conn.getCompany();
			if(rs != null)
			{
				validateUser();
				rs.first();
				this.company = new Company(rs.getString("company_name"),
											rs.getString("reg_no"),
											rs.getInt("start_week"),
											rs.getInt("start_year"));
				
				this.weekNo=this.company.getStartWeek();
				this.year=this.company.getYear();
				
				tsWeekTf.setText(String.valueOf(this.weekNo));
				tsYearTf.setText(String.valueOf(this.year));
				setTitle("Payroll " + this.company.getYear() + " "+ this.company.getCompName());
				addCompItem.setEnabled(false);
				delCompItem.setEnabled(true);
				
				rs = conn.getAllEmployees();
				if(rs.next())
				{
					this.emp = getEmployee(rs);
					disableInterface(0);
					fillCombo();
					displayEmpDetails(this.emp);
				}
				else
				{
					disableInterface(3);
					throw new NoEmployeeFoundException();
				}
			}
			else
			{
				System.out.println(rs);
				setTitle("Payroll");
				disableInterface(1);
				delCompItem.setEnabled(false);
				addCompItem.setEnabled(true);
				throw new NoCompanyFoundException();
			}
		}
		catch(NoEmployeeFoundException ne)
	    {
	    	JOptionPane.showMessageDialog(null, ne.getMessage(),
	    									"No employee", JOptionPane.ERROR_MESSAGE);
	    }
		catch(NoCompanyFoundException nc)
	    {
	    	JOptionPane.showMessageDialog(null, nc.getMessage(),
	    									"No company", JOptionPane.ERROR_MESSAGE);
	    }
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Fills search combo box with employee surname and employee number
	public void fillCombo()
	{
        	try
			{
        		rs.beforeFirst();
        		while(rs.next())
        		{
        			surname.add(rs.getString("emp_surname") + " no: " + rs.getInt("emp_no"));
        		}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
	}
	//Refills search combo box with employee surname and employee number after new employee is added
	public void reFillCombo()
	{
		for(int i=0; i<payrollModel.getSize(); i++)
		{
			payrollModel.removeElementAt(i);
		}
        	try
			{
        		rs = conn.getAllEmployees();
        		while(rs.next())
        		{
        			payrollModel.addElement(rs.getString("emp_surname") + " no: " + rs.getInt("emp_no"));
        		}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
	}
	
	//Disable interface until acceptable login details have been provided
	public void disableInterface(int i)
	{
		if(i == 1)
		{
			addBt.setEnabled(false);
			updateBt.setEnabled(false);
			ceaseBt.setEnabled(false);
			nextBt.setEnabled(false);
			prevBt.setEnabled(false);
			tsSubmitBut.setEnabled(false);
			tsClearBut.setEnabled(false);
			tsCalcPayBut.setEnabled(false);
			tsNextWeekBut.setEnabled(false);
			tsPrevWeekBut.setEnabled(false);
			p30Rb.setEnabled(false);
			p35Rb.setEnabled(false);
			p60Rb.setEnabled(false);
			p45Rb.setEnabled(false);
			weeklyReportRb.setEnabled(false);
			searchBut.setEnabled(false);
		}
		//Enable full user interface
		else if (i == 0)
		{
			addBt.setEnabled(true);
			updateBt.setEnabled(true);
			ceaseBt.setEnabled(true);
			nextBt.setEnabled(true);
			prevBt.setEnabled(true);
			tsSubmitBut.setEnabled(true);
			tsClearBut.setEnabled(true);
			tsCalcPayBut.setEnabled(true);
			tsNextWeekBut.setEnabled(true);
			tsPrevWeekBut.setEnabled(true);
			p30Rb.setEnabled(true);
			p35Rb.setEnabled(true);
			p60Rb.setEnabled(true);
			p45Rb.setEnabled(true);
			weeklyReportRb.setEnabled(true);
			searchBut.setEnabled(true);
		}
		//Disable interface partially to allow for adding new employee only
		else
		{
			addBt.setEnabled(true);
			updateBt.setEnabled(false);
			ceaseBt.setEnabled(false);
			nextBt.setEnabled(false);
			prevBt.setEnabled(false);
			tsSubmitBut.setEnabled(false);
			tsClearBut.setEnabled(false);
			tsCalcPayBut.setEnabled(false);
			tsNextWeekBut.setEnabled(false);
			tsPrevWeekBut.setEnabled(false);
			p30Rb.setEnabled(false);
			p35Rb.setEnabled(false);
			p60Rb.setEnabled(false);
			p45Rb.setEnabled(false);
			weeklyReportRb.setEnabled(false);
			searchBut.setEnabled(false);
		}
	}
	
	//Validate the users login details. They have 3 attempts before the system is locked.
	public boolean validateUser()
	{
		int attempts=3;
		icon = new ImageIcon(getClass().getResource("/logo2.png"));
		img = icon.getImage();
		boolean validated = false;
		JPanel panel = new JPanel();
		ImagePanel imgPanel = new ImagePanel(img, 0,0,250, 150);
		panel.setPreferredSize(new Dimension(250,250));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JLabel aliasLbl = new JLabel("Username:");
		JTextField aliasTf = new JTextField(20);
		aliasTf.setMaximumSize(aliasTf.getPreferredSize());
		JLabel passLbl = new JLabel("Password:");
		JPasswordField passwordTf = new JPasswordField(20);
		passwordTf.setMaximumSize(passwordTf.getPreferredSize());
		panel.add(imgPanel);
		panel.add(aliasLbl);
		panel.add(aliasTf);
		panel.add(passLbl);
		panel.add(passwordTf);
		
		do
		{
		int option = JOptionPane.showConfirmDialog(null, panel, "User Login",JOptionPane.OK_CANCEL_OPTION, 
													JOptionPane.PLAIN_MESSAGE);
			if(option == JOptionPane.OK_OPTION)
			{
				try
				{
					if(passwordTf.getPassword().length==0 || aliasTf.getText().isEmpty())
					{
						attempts--;
						throw new EmptyFieldException();
					}
					else
					{
						this.user = conn.getPayrollUser(aliasTf.getText());
						if(this.user != null)
						{
							char[] pass = this.user.getPassword().toCharArray();
							
							if(Arrays.equals(passwordTf.getPassword(),pass))
							{
								String message = "Welcome " + this.user.getFName() + " " + this.user.getSName()
									+ ". Have a nice day!";
								JOptionPane.showMessageDialog(null, message);
								validated = true;
								attempts=0;
							}
							else
							{
								attempts--;
								throw new InvalidLoginException();
							}
						}
						else
						{
							attempts--;
							throw new InvalidLoginException();
						}
					}
				}
				catch(InvalidLoginException ie)
				{
					JOptionPane.showMessageDialog(null, ie.getMessage() + " Attempts left: " + attempts, 
													"Wrong Details", JOptionPane.ERROR_MESSAGE);
				}
				catch(EmptyFieldException ee)
				{
					JOptionPane.showMessageDialog(null, ee.getMessage() + " Attempts left: " + attempts,
													"Empty fields", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				System.exit(0);
			}
		}
		while(attempts>0);
		return validated;
	}
	
	//Display and text formatting for P30
	public void showP30()
	{
		try
		{
			reportTa.setText("");
			reportTa.append("\t\tP30");
			reportTa.append("\n_________________________________________________");
			reportTa.append(this.company.toString());
			
			reportRs = conn.getReport("p30", 0);
			if(reportRs.next())
			{
				reportTa.append("\n\nTotal tax liability:\t" + String.valueOf(reportRs.getDouble("sum(tot_tax_paid)")));
				reportTa.append("\nTotal PRSI liability:\t" + String.valueOf(reportRs.getDouble("sum(tot_prsi_paid)")));
				reportTa.append("\nTotal:\t\t" + (String.valueOf(reportRs.getDouble("sum(tot_prsi_paid)")+
													reportRs.getDouble("sum(tot_tax_paid)"))));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Display and text formatting for P35
	public void showP35()
	{
		try
		{
			reportTa.setText("");
			reportTa.append("\t\tP35");
			reportTa.append("\n_________________________________________________");
			reportTa.append(this.company.toString());
			
			reportRs = conn.getReport("p35", 0);
			if(reportRs.next())
			{
				reportTa.append("\n\nTotal tax liability:\t" + String.valueOf(reportRs.getDouble("sum(tot_tax_paid)")));
				reportTa.append("\nTotal PRSI liability:\t" + String.valueOf(reportRs.getDouble("sum(tot_prsi_paid)")));
				reportTa.append("\nTotal:\t\t" + (String.valueOf(reportRs.getDouble("sum(tot_prsi_paid)")+
													reportRs.getDouble("sum(tot_tax_paid)"))));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Display and text formatting for P60
	public void showP60()
	{
		try
		{
			reportTa.setText("");
			reportTa.append("\t\tP60");
			reportTa.append("\n_________________________________________________");
			reportTa.append("\n");
			reportTa.append(this.emp.toString());
			
			reportRs = conn.getReport("p60", this.emp.getEmpNo());
			if(reportRs.next())
			{
				reportTa.append("\n\nTotal gross pay:\t" + String.valueOf(reportRs.getDouble("sum(gross_pay)")));
				reportTa.append("\nTotal net pay:\t\t" + String.valueOf(reportRs.getDouble("sum(net_pay)")));
				reportTa.append("\nTotal tax:\t\t" + String.valueOf(reportRs.getDouble("sum(tax_paid)")));
				reportTa.append("\nTotal PRSI:\t\t" + String.valueOf(reportRs.getDouble("sum(prsi_paid)")));
				reportTa.append("\nTotal USC:\t\t" + String.valueOf(reportRs.getDouble("sum(usc_paid)")));
				reportTa.append("\nPRSI class:\t\t" + this.revDet.getPrsiClass());
				reportTa.append("\nTotal weeks:\t\t" + String.valueOf(reportRs.getInt("count(week_no)")));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Display and text formatting for P45
	public void showP45()
	{
		reportTa.setText("");
		if(this.emp.getFinishDate() == null || this.emp.getFinishDate().isEmpty())
		{
			reportTa.append("No P45 available");
		}
		else
		{
			try
			{
				reportTa.append("\t\tP45");
				reportTa.append("\n_________________________________________________");
				reportTa.append(this.company.toString());
				reportTa.append("\n");
				reportTa.append(this.emp.toString());
				
				reportRs = conn.getReport("p45", this.emp.getEmpNo());
				if(reportRs.next())
				{
					//reportRs.next();
					reportTa.append("\n\nTotal gross pay:\t" + String.valueOf(reportRs.getDouble("sum(gross_pay)")));
					reportTa.append("\nTotal net pay:\t\t" + String.valueOf(reportRs.getDouble("sum(net_pay)")));
					reportTa.append("\nTotal tax:\t\t" + String.valueOf(reportRs.getDouble("sum(tax_paid)")));
					reportTa.append("\nTotal PRSI:\t\t" + String.valueOf(reportRs.getDouble("sum(prsi_paid)")));
					reportTa.append("\nTotal USC:\t\t" + String.valueOf(reportRs.getDouble("sum(usc_paid)")));
					reportTa.append("\nPRSI class:\t\t" + this.revDet.getPrsiClass());
					reportTa.append("\nTotal weeks:\t\t" + String.valueOf(reportRs.getInt("count(week_no)")));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	//Display and text formatting for Weekly Report
	public void showWeeklyReport()
	{
		reportTa.setText("");
		try
		{
			reportRs = conn.getReport("w", this.weekNo);
			if(reportRs.next())
			{
				reportTa.append("\t\tWeekly report");
				reportTa.append("\n_________________________________________________");
				reportTa.append("\n\tWeek no:\t" + reportRs.getString("week_no"));
				reportTa.append("\n\nTotal gross pay:\t" + String.valueOf(reportRs.getDouble("sum(gross_pay)")));
				reportTa.append("\nTotal net pay:\t\t" + String.valueOf(reportRs.getDouble("sum(net_pay)")));
				reportTa.append("\nTotal tax:\t\t" + String.valueOf(reportRs.getDouble("sum(tax_paid)")));
				reportTa.append("\nTotal PRSI:\t\t" + String.valueOf(reportRs.getDouble("sum(prsi_paid)")));
				reportTa.append("\nTotal USC:\t\t" + String.valueOf(reportRs.getDouble("sum(usc_paid)")));
			}
			else
			{
				reportTa.append("No report available");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Display new JFrame to allow user to create a new company and commit this to the database
	public void createCompany()
	{
		JPanel newCompPanel = new JPanel();
		newCompPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		newCompPanel.setPreferredSize(new Dimension(200,220));
		JLabel compNameLbl = new JLabel("Company name:");
        JLabel regNoLbl = new JLabel("Registration no:");
        JLabel weekNoLbl = new JLabel("Starting week no:");
        JLabel yearLbl = new JLabel("Year:");
        JTextField compNameTf = new JTextField(15);
        JTextField regNoTf = new JTextField(15);
        JTextField weekNoTf = new JTextField(15);
        JTextField yearTf = new JTextField(15);
        newCompPanel.add(compNameLbl);
        newCompPanel.add(compNameTf);
        newCompPanel.add(regNoLbl);
        newCompPanel.add(regNoTf);
        newCompPanel.add(weekNoLbl);
        newCompPanel.add(weekNoTf);
        newCompPanel.add(yearLbl);
        newCompPanel.add(yearTf);
        boolean registered;
        
        do
        {
        	registered = false;
	        int option = JOptionPane.showConfirmDialog(null, newCompPanel,"Required details", 
					JOptionPane.OK_CANCEL_OPTION);
	        
	        if(option == JOptionPane.OK_OPTION)
	        {
	        	try
	        	{
		        	if(!compNameTf.getText().isEmpty()
		        		&& !regNoTf.getText().isEmpty() 
		        		&& !weekNoTf.getText().isEmpty()
		        		&& !yearTf.getText().isEmpty())
		        	{
		        		//Database length constraints
		        		if(compNameTf.getText().length()>30)
		        		{
		        			throw new InputLengthExceededException(30, "Company name");
		        		}
		        		if(regNoTf.getText().length()>10)
		        		{
		        			throw new InputLengthExceededException(10, "Registration number");
		        		}
		        		if(yearTf.getText().length()>4)
		        		{
		        			throw new InputLengthExceededException(4, "Year");
		        		}
		        		
		        		int week = Integer.parseInt(weekNoTf.getText());
		        		if(week >=1 && week<=52)
		        		{
		        			//Week between 1 and 52
			        		this.company = new Company(compNameTf.getText(),
			        							regNoTf.getText(), 
			        							Integer.parseInt(weekNoTf.getText()),
			        							Integer.parseInt(yearTf.getText()));
			    			conn.insertCompany(this.company);
			    			registered = true;
			    			startPayroll();
		        		}
		        		else
		        		{
		        			throw new WeekOutOfRangeException();
		        		}
		        	}
		        	else
		        	{
		        		throw new EmptyFieldException();
		        	}
	        	}
	        	catch(InputLengthExceededException ie)
	        	{
	        		JOptionPane.showMessageDialog(null, ie.getMessage(),
							"Value too long", JOptionPane.ERROR_MESSAGE);
	        	}
	        	catch(EmptyFieldException ee)
	        	{
	        		JOptionPane.showMessageDialog(null, ee.getMessage(),
	        										"Empty field", JOptionPane.ERROR_MESSAGE);
	        	}
	        	catch(WeekOutOfRangeException we)
	        	{
	        		JOptionPane.showMessageDialog(null, we.getMessage(),
	        										"Week out of range", JOptionPane.ERROR_MESSAGE);
	        	}
	        	catch(NumberFormatException ne)
		    	{
					JOptionPane.showMessageDialog(null,"Wrong input", "Invalid input", JOptionPane.ERROR_MESSAGE);
		    	}
	        }
	        else
	        {
	        	registered = true;
	        }
        }
        while(!registered);
	}

	//Delete company from the program and delete all database entries relating to this company
	public void deleteCompany()
	{
		int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this company?" +
							"\n System must close in order to proceed.",this.company.getCompName(), JOptionPane.OK_CANCEL_OPTION);
        
        if(option == JOptionPane.OK_OPTION)
        {
        	conn.deleteCompany();
        	conn.disconnect();
        	System.exit(0);
        }
	}
	
	//Getter for RevenueDetails
	public RevenueDetails getRevDetails(ResultSet rs)
	{
		RevenueDetails rev = null;
		try
		{
			rev = new RevenueDetails(rs.getDouble("cut_off"), 
									 rs.getDouble("tax_credit"), 
									 rs.getString("prsi_class"));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return rev;
	}
	
	//Getter for BankDetails
	public BankDetails getBankDetails(ResultSet rs)
	{
		BankDetails bank = null;
		try
		{
			bank = new BankDetails(rs.getString("bank_name"),
								   rs.getString("account_no"),
								   rs.getString("sort_code"));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return bank;
	}
	
	//Getter method for Employee
	public Employee getEmployee(ResultSet rs)
	{
		this.revDet = getRevDetails(rs);
		this.bankDet = getBankDetails(rs);
		
		Employee employee = null;
		try
		{
				int empNo = rs.getInt("emp_no");
				String sname = rs.getString("emp_surname");
				String fname = rs.getString("emp_fname");
				String dob = rs.getString("emp_dob");
				String pps = rs.getString("emp_pps");
				String addL1 = rs.getString("emp_address_l1");
				String addL2 = rs.getString("emp_address_l2");
				String addL3 = rs.getString("emp_address_l3");
				String email = rs.getString("emp_email");
				String phone = rs.getString("emp_phone");
				String sDate = rs.getString("emp_start_date");
				String fDate = rs.getString("emp_finish_date");
				String dept = rs.getString("emp_department");
				String payment = rs.getString("emp_pay_type");
				double rate = rs.getDouble("emp_rate");
				String check = rs.getString("emp_hourly");
				
				if(check.equalsIgnoreCase("N"))
				{
					employee = new SalariedEmployee(empNo, sname, fname, addL1,addL2,addL3,email, phone,
													dob, pps, sDate, fDate, dept,payment, rate);
				}
				else
				{
					employee = new HourlyEmployee(empNo, sname, fname, addL1,addL2,addL3,email, phone,
													dob, pps, sDate, fDate, dept, payment, rate);
				}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return employee;
	}
	
	//Displays employee details for the specified employee
	public void displayEmpDetails(Employee e)
	{
		displayTimeSheet(e, this.weekNo, this.year);
		tsEmpNameTf.setText(e.getFirstName() + " " + e.getSurname());
		surnameCbox.setSelectedItem(e.getSurname() + " no: " + e.getEmpNo());
		payrollModel.actionPerformed(new ActionEvent(this,0,"update"));
		surnameTf.setText(e.getSurname());
		empNoTf.setText(String.valueOf(e.getEmpNo()));
		firstNameTf.setText(e.getFirstName());
		dobTf.setText(e.getDob());
		ppsTf.setText(e.getPps());
		startDateTf.setText(e.getStartDate());
		finishDateTf.setText(e.getFinishDate());
		addressLine1Tf.setText(e.getAddressL1());
		addressLine2Tf.setText(e.getAddressL2());
		addressLine3Tf.setText(e.getAddressL3());
		emailTf.setText(e.getEmail());
		phoneTf.setText(e.getPhone());
		departmentCbox.setSelectedItem(e.getDepartment());
		
		if(e.getFinishDate() != null)
		{
			updateBt.setEnabled(false);
			ceaseBt.setEnabled(false);
			finishDateTf.setBackground(new Color(255,100,100));
		}
		else
		{
			updateBt.setEnabled(true);
			ceaseBt.setEnabled(true);
			finishDateTf.setBackground(new Color(255,255,255));
		}
		
		if(e.getPaymentType().equalsIgnoreCase("transfer"))
		{
			transferRb.setSelected(true);
			accountNoTf.setText(this.bankDet.getAccountNo());
			sortCodeTf.setText(this.bankDet.getSortCode());
			bankNameTf.setText(this.bankDet.getBankName());
		}
		else if(e.getPaymentType().equalsIgnoreCase("cash"))
		{
			cashRb.setSelected(true);
			accountNoTf.setText("");
			sortCodeTf.setText("");
			bankNameTf.setText("");
		}
		else
		{
			chequeRb.setSelected(true);
			accountNoTf.setText("");
			sortCodeTf.setText("");
			bankNameTf.setText("");
		}
		
		cutOffTf.setText(Double.toString(this.revDet.getCutOffPoint()));
		taxCreditTf.setText(Double.toString(this.revDet.getTaxCredit()));
		prsiCbox.setSelectedItem(this.revDet.getPrsiClass());
		
		if(e instanceof HourlyEmployee)
        {
        	disableOptions(false);
			HourlyEmployee he = (HourlyEmployee)e;
			hourlyRateTf.setText(Double.toString(he.getRate()));
			weeklyPayTf.setText("");
        }
        else if(e instanceof SalariedEmployee)
        {
        	disableOptions(true);
			SalariedEmployee se = (SalariedEmployee)e;
			weeklyPayTf.setText(Double.toString(se.getSalary()));
			hourlyRateTf.setText("");
        }
        totEmpTf.setText(String.valueOf(DbConn.TOT_EMPLOYEE));
	}
	
	//Display Timesheet Details
	public void displayTimeSheet(Employee emp, int week, int year) throws WeekOutOfRangeException
	{
		try
		{
			if(week<1)
			{
				tsWeekTf.setText(String.valueOf(++this.weekNo));
				throw new WeekOutOfRangeException();
			}
			else if(week>52)
			{
				tsWeekTf.setText(String.valueOf(--this.weekNo));
				throw new WeekOutOfRangeException();
			}
			
			TimeSheet ts = conn.getTimesheet(emp, week, year);
			WeeklyPay wp = conn.getWeeklyPay(emp, week, year);
			
			if(ts != null && wp != null)
			{
				tsSubmitBut.setEnabled(false);
				tsCalcPayBut.setEnabled(false);
				paySummaryTa.setText("");
				paySummaryTa.append(ts.toString());
				paySummaryTa.append(wp.toString());
			}
			else
			{
				tsSubmitBut.setEnabled(true);
				tsCalcPayBut.setEnabled(true);
				paySummaryTa.setText("No timesheet for week " + this.weekNo + " in database.");
			}
			
			if(this.weekNo<this.company.getStartWeek())
			{
				tsSubmitBut.setEnabled(false);
				tsCalcPayBut.setEnabled(false);
			}
		}
		catch(WeekOutOfRangeException we)
		{
			JOptionPane.showMessageDialog(null, we.getMessage());
		}
	}
	
	//Disable selectable options in the GUI
	public void disableOptions(boolean disable)
	{
		if(disable)
		{
			for(int i=0;i<7;i++)
			{
				startHCbox[i].setEnabled(false);
				startMCbox[i].setEnabled(false);
				finishHCbox[i].setEnabled(false);
				finishMCbox[i].setEnabled(false);
			}
			weeklyPayRb.setSelected(true);
        	weeklyPayTf.setEditable(true);
			hourlyRateTf.setEditable(false);
		}
		else
		{
			for(int i=0;i<7;i++)
			{
				startHCbox[i].setEnabled(true);
				startMCbox[i].setEnabled(true);
				finishHCbox[i].setEnabled(true);
				finishMCbox[i].setEnabled(true);
			}
			hourlyRateRb.setSelected(true);
        	weeklyPayTf.setEditable(false);
			hourlyRateTf.setEditable(true);
		}
	}
	
	//Calculate total hours from weekly timesheet hours
	public double calculateTotHours()
	{
		double totHours=0;
		double totMinutes=0;
		
		for(int i=0; i<startHCbox.length; i++)
		{
			double startH = Double.parseDouble((String) startHCbox[i].getSelectedItem());
			double finishH = Double.parseDouble((String) finishHCbox[i].getSelectedItem());
			double startM = Double.parseDouble((String) startMCbox[i].getSelectedItem());
			double finishM = Double.parseDouble((String) finishMCbox[i].getSelectedItem());
			
			if(finishH < startH)
			{
				finishH+=24;
			}
			if(finishM < startM)
			{
				if(startH != finishH)
				{
					finishM+=60;
					--finishH;
				}
				else
				{
					finishM-=40;
					finishH+=24;
				}
			}
			totHours += (finishH - startH);
			totMinutes += (finishM - startM);
			if(totMinutes >= 60)
			{
				totHours++;
				totMinutes-=60;
			}
		}
		return totHours+(totMinutes/100);
	}	
	
	/*
	 * Method to create a new JFrame to add a new employee to the database. 
	 * Only basic details required to create an employee, other details may
	 * be added by updating the employee
	 */
	public void addEmployee()
	{		
		class NewEmployee implements ActionListener
		{
			JFrame frame;
			JButton okBut;
			JButton cancelBut;
			JLabel newFnameLbl,newSurnameLbl, newPpsLbl,newDobLbl,newAddressLbl,newSDateLbl;
			JTextField newFnameTf ,newSurnameTf ,newPpsTf , newDobTf,newAddressL1Tf, newAddressL2Tf , newAddressL3Tf,
					newSDateTf;
			JRadioButton hourlyRb, salariedRb;
			Employee employee = null;
			
			public NewEmployee()
			{
				frame = new JFrame();
				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				JPanel newEmpPanel = new JPanel();
				newEmpPanel.setLayout(new BoxLayout(newEmpPanel, BoxLayout.PAGE_AXIS));
				newEmpPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
				newFnameLbl = new JLabel("First name:");
				newSurnameLbl = new JLabel("Surname:");
				newPpsLbl = new JLabel("Pps No:");
		        newDobLbl = new JLabel("DOB:");
		        newAddressLbl = new JLabel("Address:");
		        newSDateLbl = new JLabel("Start date:");
		        newFnameTf = new JTextField(15);
				newSurnameTf = new JTextField(15);
		        newPpsTf = new JTextField(10);
		        newDobTf = new JTextField(10);
		        newAddressL1Tf = new JTextField(20);
		        newAddressL2Tf = new JTextField(20);
		        newAddressL3Tf = new JTextField(20);
		        newSDateTf = new JTextField(10);
				hourlyRb = new JRadioButton("Hourly employee");
				hourlyRb.setSelected(true);
				salariedRb = new JRadioButton("Salaried employee");
				ButtonGroup newEmpGroup = new ButtonGroup();
				okBut = new JButton("Ok");
				okBut.addActionListener(this);
				cancelBut = new JButton("Cancel");
				cancelBut.addActionListener(this);
				JPanel butPanel = new JPanel();
				butPanel.add(okBut);
				butPanel.add(cancelBut);
				newEmpGroup.add(hourlyRb);
				newEmpGroup.add(salariedRb);
				newEmpPanel.add(newFnameLbl);
				newEmpPanel.add(newFnameTf);
				newEmpPanel.add(newSurnameLbl);
				newEmpPanel.add(newSurnameTf);
				newEmpPanel.add(newPpsLbl);
				newEmpPanel.add(newPpsTf);
				newEmpPanel.add(newDobLbl);
				newEmpPanel.add(newDobTf);
				newEmpPanel.add(newAddressLbl);
				newEmpPanel.add(newAddressL1Tf);
				newEmpPanel.add(newAddressL2Tf);
				newEmpPanel.add(newAddressL3Tf);
				newEmpPanel.add(hourlyRb);
				newEmpPanel.add(salariedRb);
				newEmpPanel.add(newSDateLbl);
				newEmpPanel.add(newSDateTf);
				frame.add(newEmpPanel,BorderLayout.CENTER);
				frame.add(butPanel,BorderLayout.SOUTH);
				frame.setVisible(true);
				frame.setSize(300,450);
				frame.setLocationRelativeTo(null);
				setEnabled(false);
			}	
			public void actionPerformed(ActionEvent ae)
			{
				JButton src = (JButton)ae.getSource();
				if(src == cancelBut)
				{
					setEnabled(true);
					frame.dispose();
				}
				else if(src == okBut)
				{
					addEmp();
				}		
			}
			
			//Method to commit the new employee to the databse.
			void addEmp()
			{
				try
				{
					String lname = newSurnameTf.getText();
					String fname = newFnameTf.getText();
					String addL1 = newAddressL1Tf.getText();
					String addL2 = newAddressL2Tf.getText();
					String addL3 = newAddressL3Tf.getText();
					String pps = newPpsTf.getText();
					String dob = newDobTf.getText();
					String email = "";
					String phone = "";
					String sDate = newSDateTf.getText();
					String fDate = "";
					String dept = "";
					String payment = "transfer";
					
					if(newSurnameTf.getText().isEmpty() 
						|| newFnameTf.getText().isEmpty() 
						|| newPpsTf.getText().isEmpty()
						|| newDobTf.getText().isEmpty())
					{
						throw new EmptyFieldException();
					}
					if(newSurnameTf.getText().length()>30
						|| newFnameTf.getText().length()>30)
					{
						throw new InputLengthExceededException(30, "First name or surname");
					}
					if(newAddressL1Tf.getText().length()>30
						|| newAddressL2Tf.getText().length()>30
						|| newAddressL3Tf.getText().length()>30)
					{
						throw new InputLengthExceededException(30, "Address");
					}
					
					if(!verifyPps(pps))
					{
						System.out.println(verifyPps(pps));
						throw new WrongPpsFormatException();
					}
					if(!validateDate(sDate) || !validateDate(dob))
					{
						throw new WrongDateFormatException();
					}
					
					if(hourlyRb.isSelected())
					{
						double rate = 0.0;
						employee = new HourlyEmployee(lname, fname, addL1, addL2, addL3, email, phone, 
														dob, pps, sDate,fDate, dept, payment, rate);
					}
					else if(salariedRb.isSelected())
					{
						double salary = 0.0;
						employee = new SalariedEmployee(lname, fname, addL1, addL2, addL3, email, phone,
														dob, pps, sDate,fDate, dept, payment, salary);
					}
					conn.insertEmployee(employee);
					frame.dispose();
					setEnabled(true);
					disableInterface(0);
					reFillCombo();
//					try
//					{
//						rs = conn.getAllEmployees();
//						rs.beforeFirst();
//						rs.next();
//						emp=getEmployee(rs);
//						//surname.add(employee.getSurname() + " no: " + employee.getEmpNo());
//						reFillCombo();
//						//payrollModel.actionPerformed(new ActionEvent(this,0,"update"));
//						displayEmpDetails(emp);
//						disableInterface(0);
//					}
//					catch(SQLException e)
//					{
//						e.printStackTrace();
//					}
				}
				catch(InputLengthExceededException ie)
				{
					JOptionPane.showMessageDialog(null, ie.getMessage(), "Value too long", JOptionPane.ERROR_MESSAGE);
				}
				catch(EmptyFieldException ee)
				{
					JOptionPane.showMessageDialog(null, ee.getMessage(), "Empty field", JOptionPane.ERROR_MESSAGE);
				}
				catch(WrongPpsFormatException pe)
				{
					JOptionPane.showMessageDialog(null, pe.getMessage(), "Pps error", JOptionPane.ERROR_MESSAGE);
				}
				catch(WrongDateFormatException de)
				{
					JOptionPane.showMessageDialog(null, de.getMessage(), "Invalid date", JOptionPane.ERROR_MESSAGE);
				}
				catch(NumberFormatException ne)
		    	{
					JOptionPane.showMessageDialog(null,"Wrong input", "Invalid input", JOptionPane.ERROR_MESSAGE);
		    	}
			}
		}
		new NewEmployee();
	}
	
	//Method to verify the format of the value entered by the user in the PPS number field
	public boolean verifyPps(String pps)
	{
		boolean verified=true;

			if(pps.length()!=8)
			{
				verified=false;
			}
			else if(!Character.isLetter(pps.charAt(7)))
			{
				verified=false;
			}
			for(int i=0; i<pps.length()-1; i++)
			{
				if(!Character.isDigit(pps.charAt(i)))
				{
					verified=false;
				}
			}
		return verified;
	}
	
	//Method to create a timesheet using values entered in the timesheet tab
	public TimeSheet createTimesheet(Employee emp)
	{
		TimeSheet ts = null;
		double hours = calculateTotHours();
		String[] times = new String[7];
		
		for(int i=0;i<times.length;i++)
		{
			times[i] = (String) startHCbox[i].getSelectedItem() + "." +(String) startMCbox[i].getSelectedItem()
					+" - "+(String) finishHCbox[i].getSelectedItem() + "." + (String) finishMCbox[i].getSelectedItem();
		}
		
		ts = new TimeSheet(emp, this.weekNo, this.year, 
							times[0],times[1],times[2],times[3],times[4],times[5],times[6],hours);
		
		paySummaryTa.setText("");
		paySummaryTa.append(ts.toString());
		
		return ts;
	}
	
	//Creates weeklypay employee
	public WeeklyPay createWeeklyPay(Employee emp)
	{
		WeeklyPay wp = null;
		double hours = calculateTotHours();
		double extra = Double.parseDouble(tsExtraPayTf.getText());
		double grossPay = emp.calcGrossPay(hours) + extra;
		double tax = emp.calcTax(grossPay, this.revDet.getCutOffPoint(), this.revDet.getTaxCredit());
		double netPay = grossPay-tax;
		double prsi = emp.calcPrsi(grossPay);
		double usc = emp.calcUsc(grossPay);
		
		wp = new WeeklyPay(emp, this.weekNo,this.year, grossPay, netPay, tax, prsi, usc);
		paySummaryTa.append(wp.toString());
		return wp;
	}
	
	/*
	 * Method to update an employee added to the database.
	 * Can add extra details here over the add eployee method
	 */
	public void updateEmpDetails(Employee e)
	{	
		int option = JOptionPane.showConfirmDialog(null,"Are you sure you want to update " 
				+ e.getFirstName() + " " + e.getSurname()+ " ?", 
				"Update employee",JOptionPane.YES_NO_OPTION);
		
		if(option == JOptionPane.YES_OPTION)
		{	try
			{
				if(!emailTf.getText().isEmpty())
				{
					if(emailTf.getText().length()<50)
					{
						if(!validateEmail(emailTf.getText()))
						{
							throw new IvalidEmailAddressException();
						}
					}
					else
					{
						throw new InputLengthExceededException(50, "Email address");
					}
				}
				if(!validateDate(dobTf.getText()))
				{
					throw new WrongDateFormatException();
				}
				if(phoneTf.getText().length()>14)
				{
					throw new InputLengthExceededException(14,"Phone number");
				}
			
				e.setSurname(surnameTf.getText());
				e.setFirstName(firstNameTf.getText());
				e.setAddressL1(addressLine1Tf.getText());
				e.setAddressL2(addressLine2Tf.getText());
				e.setAddressL3(addressLine3Tf.getText());
				e.setEmail(emailTf.getText());
				e.setPhone(phoneTf.getText());
				e.setDob(dobTf.getText());
				e.setPps(ppsTf.getText());
				e.setStartDate(startDateTf.getText());
				e.setDepartment((String)departmentCbox.getSelectedItem());
				
				if(e instanceof HourlyEmployee)
				{
					HourlyEmployee he = (HourlyEmployee)e;
					he.setRate(Double.parseDouble(hourlyRateTf.getText()));
				}
				else if(e instanceof SalariedEmployee)
				{
					SalariedEmployee se = (SalariedEmployee)e;
					se.setSalary(Double.parseDouble(weeklyPayTf.getText()));
				}
				
				String account="";
				String sortCode="";
				String bank="";
				
				if(transferRb.isSelected())
				{
					e.setPaymentType("Transfer");
					account = accountNoTf.getText();
					sortCode = sortCodeTf.getText();
					bank = bankNameTf.getText();
				}
				else if(cashRb.isSelected())
				{
					e.setPaymentType("Cash");
				}
				else if(chequeRb.isSelected())
				{
					e.setPaymentType("Cheque");
				}
				
				double cutOff = Double.parseDouble(cutOffTf.getText());
				double taxCredit = Double.parseDouble(taxCreditTf.getText());
				String prsi = (String) prsiCbox.getSelectedItem();
				conn.updateEmpDetails(e, cutOff, taxCredit, prsi, bank, account, sortCode); //connects to db to update details
				
				rs = conn.getAllEmployees();
				try
				{
					rs.beforeFirst();
					rs.next();
					this.emp = getEmployee(rs);
					displayEmpDetails(this.emp);
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
			}
			catch(InputLengthExceededException ie)
			{
				JOptionPane.showMessageDialog(null, ie.getMessage(), "Input too long", JOptionPane.ERROR_MESSAGE);
			}
			catch(IvalidEmailAddressException ie)
			{
				JOptionPane.showMessageDialog(null, ie.getMessage(), "Invalid email", JOptionPane.ERROR_MESSAGE);
			}
			catch(WrongDateFormatException we)
			{
				JOptionPane.showMessageDialog(null, we.getMessage(), "Invalid date", JOptionPane.ERROR_MESSAGE);
			}
			catch(NumberFormatException ne)
	    	{
				JOptionPane.showMessageDialog(null,"Wrong input", "Invalid input", JOptionPane.ERROR_MESSAGE);
	    	}
		}
	}
	
	//Method to validate format of the value entered by the user in the email field
	public boolean validateEmail(String email)
	{
		boolean validated=false;
		if(email.contains("@") && email.contains("."))
		{
			validated=true;
		}
		return validated;
	}
	
	//Method to cease employees employment
	public void ceaseEmployment(Employee e)
	{
		JPanel panel = new JPanel();
		JTextField fDateTf = new JTextField(10);
		panel.add(new JLabel("Finish date"));
		panel.add(fDateTf);
		boolean ceased = false;
		do
		{
			try
			{
			int option = JOptionPane.showConfirmDialog(null,panel, 
														"Enter finish date",JOptionPane.OK_CANCEL_OPTION);
			if(option == JOptionPane.OK_OPTION)
			{
				if(validateDate(fDateTf.getText()))
				{
					ceased=true;
					e.setFinishDate(fDateTf.getText());
					conn.ceaseEmployment(e);
					rs = conn.getAllEmployees();
					try
					{
						rs.beforeFirst();
						rs.next();
						this.emp = getEmployee(rs);
						displayEmpDetails(this.emp);
					}
					catch (SQLException e1)
					{
						e1.printStackTrace();
					}
				}
				else
				{	
					throw new WrongDateFormatException();
				}
			}
			else if(option == JOptionPane.CANCEL_OPTION)
			{
				ceased=true;
			}
		}
		catch(WrongDateFormatException we)
		{
			JOptionPane.showMessageDialog(null, we.getMessage(), "Invalid date", JOptionPane.ERROR_MESSAGE);
		}
	}
	while(ceased == false);
	}
	
	//Method to validate dates entered by the user
	public boolean validateDate(String date)
	{
		boolean validated = true;
		if(date.length()!=10)
		{
			validated = false;
		}
		else if(!Character.isDigit(date.charAt(0)) 
				|| !Character.isDigit(date.charAt(1))
				|| !Character.isDigit(date.charAt(3))
				|| !Character.isDigit(date.charAt(4))
				|| !Character.isDigit(date.charAt(6))
				|| !Character.isDigit(date.charAt(7))
				|| !Character.isDigit(date.charAt(8))
				|| !Character.isDigit(date.charAt(9)))
			
		{
			validated = false;
		}
		return validated;
	}
	
	//Instantiates and runs the GUI with the "Nimbus" style installed
    public static void main(String[] args)
    {
    	try
    	{
    		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
    		{
    	        if ("Nimbus".equals(info.getName())) 
    	        {
    	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//    	            UIManager.setLookAndFeel(info.getClassName());
    	            break;
    	        }    	
    	     }
    	}
    	catch(UnsupportedLookAndFeelException e)
    	{
    		e.printStackTrace();
    	}
    	catch (ClassNotFoundException e) 
    	{
    		e.printStackTrace();    	    
    	}
    	catch (InstantiationException e) 
    	{
    	    e.printStackTrace();    	    
    	}
    	catch (IllegalAccessException e) 
    	{
    	    e.printStackTrace();    	    
    	}
    	
    	PayrollGui pg = new PayrollGui();
		pg.setVisible(true);
    }
}
