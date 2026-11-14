package ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Colours and fonts shared by every screen, so the look stays consistent.
 *
 * @author Shirabthinath Shivaji
 * @version 2.0
 */
public final class Theme {

    /** Dark header colour (Northeastern red). */
    public static final Color HEADER = new Color(0xC8, 0x10, 0x2E);

    /** Background of the screens. */
    public static final Color BACKGROUND = new Color(0xF4, 0xF5, 0xF7);

    /** Colour of the required-field star. */
    public static final Color REQUIRED = new Color(0xC8, 0x10, 0x2E);

    /** Not meant to be instantiated. */
    private Theme() {
    }

    /**
     * Creates a large bold screen title.
     *
     * @param text the title text
     * @return the label
     */
    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
        return label;
    }

    /**
     * Creates a button with a fixed comfortable size.
     *
     * @param text the button text
     * @return the button
     */
    public static JButton button(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }
}
