import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Custom Date Picker Component for NetBeans Palette
 * Usage: DatePicker dp = new DatePicker();
 *        Date selected = dp.getDate();
 *        String text = dp.getDateString("yyyy-MM-dd");
 */
public class DatePicker extends JPanel {

    private JTextField dateField;
    private JButton calendarButton;
    private JDialog calendarDialog;
    private Calendar selectedCalendar;
    private String dateFormat = "yyyy-MM-dd";

    // Colors — customize these to match your UI
    private Color headerColor = new Color(46, 139, 87);   // green header
    private Color selectedColor = new Color(46, 139, 87); // selected day
    private Color todayColor = new Color(255, 165, 0);    // today highlight

    public DatePicker() {
        initComponent();
    }

    private void initComponent() {
        selectedCalendar = Calendar.getInstance();
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // Text field showing selected date
        dateField = new JTextField();
        dateField.setEditable(false);
        dateField.setPreferredSize(new Dimension(150, 30));
        dateField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateField.setText(new SimpleDateFormat(dateFormat).format(new Date()));

        // Calendar button
        calendarButton = new JButton("📅");
        calendarButton.setPreferredSize(new Dimension(35, 30));
        calendarButton.setFocusPainted(false);
        calendarButton.setBackground(headerColor);
        calendarButton.setForeground(Color.WHITE);
        calendarButton.setBorder(BorderFactory.createEmptyBorder());
        calendarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        calendarButton.addActionListener(e -> showCalendar());
        dateField.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { showCalendar(); }
        });

        add(dateField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
    }

    private void showCalendar() {
        // Find parent window
        Window parent = SwingUtilities.getWindowAncestor(this);
        calendarDialog = new JDialog(parent, "Select Date", Dialog.ModalityType.APPLICATION_MODAL);
        calendarDialog.setUndecorated(true);
        calendarDialog.add(buildCalendarPanel());
        calendarDialog.pack();

        // Position below the field
        Point loc = getLocationOnScreen();
        calendarDialog.setLocation(loc.x, loc.y + getHeight());
        calendarDialog.setVisible(true);
    }

    private JPanel buildCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new LineBorder(headerColor, 2));

        // --- Header: Month/Year navigation ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(headerColor);
        header.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JButton prevBtn = navButton("◀");
        JButton nextBtn = navButton("▶");

        JLabel monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setForeground(Color.WHITE);
        monthYearLabel.setFont(new Font("Arial", Font.BOLD, 14));

        prevBtn.addActionListener(e -> {
            selectedCalendar.add(Calendar.MONTH, -1);
            refreshCalendar(panel, monthYearLabel);
        });
        nextBtn.addActionListener(e -> {
            selectedCalendar.add(Calendar.MONTH, 1);
            refreshCalendar(panel, monthYearLabel);
        });

        header.add(prevBtn, BorderLayout.WEST);
        header.add(monthYearLabel, BorderLayout.CENTER);
        header.add(nextBtn, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // --- Day grid ---
        JPanel gridPanel = new JPanel(new GridLayout(7, 7, 2, 2));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridPanel.setName("grid");
        panel.add(gridPanel, BorderLayout.CENTER);

        // --- Close button ---
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(Color.LIGHT_GRAY);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> calendarDialog.dispose());
        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        footer.add(closeBtn);
        panel.add(footer, BorderLayout.SOUTH);

        refreshCalendar(panel, monthYearLabel);
        return panel;
    }

    private void refreshCalendar(JPanel panel, JLabel monthYearLabel) {
        // Update month/year label
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        monthYearLabel.setText(sdf.format(selectedCalendar.getTime()));

        // Find grid panel
        JPanel gridPanel = null;
        for (Component c : panel.getComponents()) {
            if (c instanceof JPanel && "grid".equals(c.getName())) {
                gridPanel = (JPanel) c;
                break;
            }
        }
        if (gridPanel == null) return;

        gridPanel.removeAll();

        // Day headers
        String[] days = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 11));
            lbl.setForeground(headerColor);
            gridPanel.add(lbl);
        }

        // Get first day and total days in month
        Calendar cal = (Calendar) selectedCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Today's info
        Calendar today = Calendar.getInstance();

        // Empty cells before first day
        for (int i = 0; i < firstDay; i++) {
            gridPanel.add(new JLabel(""));
        }

        // Day buttons
        for (int day = 1; day <= totalDays; day++) {
            final int d = day;
            JButton btn = new JButton(String.valueOf(day));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Highlight today
            boolean isToday = (day == today.get(Calendar.DAY_OF_MONTH)
                    && selectedCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                    && selectedCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR));

            if (isToday) {
                btn.setBackground(todayColor);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.DARK_GRAY);
            }

            btn.addActionListener(e -> {
                selectedCalendar.set(Calendar.DAY_OF_MONTH, d);
                dateField.setText(new SimpleDateFormat(dateFormat).format(selectedCalendar.getTime()));
                calendarDialog.dispose();
            });

            // Hover effect
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(selectedColor);
                    btn.setForeground(Color.WHITE);
                }
                public void mouseExited(MouseEvent e) {
                    if (isToday) {
                        btn.setBackground(todayColor);
                    } else {
                        btn.setBackground(Color.WHITE);
                        btn.setForeground(Color.DARK_GRAY);
                    }
                }
            });

            gridPanel.add(btn);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JButton navButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(headerColor);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ===== PUBLIC METHODS =====

    /** Get selected date as Date object */
    public Date getDate() {
        return selectedCalendar.getTime();
    }

    /** Get selected date as formatted String */
    public String getDateString(String format) {
        return new SimpleDateFormat(format).format(selectedCalendar.getTime());
    }

    /** Get selected date as default format (yyyy-MM-dd) */
    public String getDateString() {
        return new SimpleDateFormat(dateFormat).format(selectedCalendar.getTime());
    }

    /** Set the date format displayed in the text field */
    public void setDateFormat(String format) {
        this.dateFormat = format;
        dateField.setText(new SimpleDateFormat(format).format(selectedCalendar.getTime()));
    }

    /** Set a specific date */
    public void setDate(Date date) {
        selectedCalendar.setTime(date);
        dateField.setText(new SimpleDateFormat(dateFormat).format(date));
    }

    /** Change the header/accent color */
    public void setAccentColor(Color color) {
        this.headerColor = color;
        this.selectedColor = color;
        calendarButton.setBackground(color);
    }
}