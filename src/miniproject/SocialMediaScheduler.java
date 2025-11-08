package miniproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

abstract class Platform {
    protected String name;
    public Platform(String name) { this.name = name; }
    public abstract void post(String message, String image);
}

interface Shareable {
    void share(String message);
}

class Facebook extends Platform implements Shareable {
    public Facebook() { super("Facebook"); }

    public void post(String message, String image) {
        System.out.println("\nFacebook Post");
        System.out.println("Message: " + message);
        if (!image.isEmpty()) System.out.println("Image: " + image);
        System.out.println("Successfully posted on Facebook");
    }

    public void share(String message) {
        System.out.println("Shared post: " + message);
    }
}

class Twitter extends Platform {
    public Twitter() { super("Twitter"); }

    public void post(String message, String image) {
        System.out.println("\nTwitter Post");
        System.out.println("Tweet: " + message);
        System.out.println("Tweeted successfully");
    }
}

class Instagram extends Platform {
    public Instagram() { super("Instagram"); }

    public void post(String message, String image) {
        System.out.println("\nInstagram Post");
        System.out.println("Caption: " + message);
        if (!image.isEmpty()) System.out.println("Image: " + image);
        System.out.println(" Posted successfully on Instagram");
    }
}

class InvalidPlatformException extends Exception {
    public InvalidPlatformException(String msg) { super(msg); }
}

class Post {
    private String content;
    private String image;
    private LocalDateTime time;
    private String platform;

    public Post(String content, String image, LocalDateTime time, String platform) {
        this.content = content;
        this.image = image;
        this.time = time;
        this.platform = platform;
    }

    public String getContent() { return content; }
    public String getImage() { return image; }
    public LocalDateTime getTime() { return time; }
    public String getPlatform() { return platform; }
}

class Scheduler extends Thread {
    private Post post;
    public Scheduler(Post post) { this.post = post; }

    public void run() {
        try {
            long delay = Duration.between(LocalDateTime.now(), post.getTime()).toMillis();
            if (delay > 0) {
                System.out.println("Waiting until scheduled time...");
                Thread.sleep(delay);
            }

            Platform platform;
            switch (post.getPlatform().toLowerCase()) {
                case "facebook" -> platform = new Facebook();
                case "twitter" -> platform = new Twitter();
                case "instagram" -> platform = new Instagram();
                default -> throw new InvalidPlatformException("Invalid platform name!");
            }

            platform.post(post.getContent(), post.getImage());
            System.out.println(" Posted at: " + LocalDateTime.now());
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }
}

public class SocialMediaScheduler {
    private JFrame frame;
    private JTextField contentField, imageField, timeField;
    private JComboBox<String> platformBox;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SocialMediaScheduler().createGUI());
    }

    public void createGUI() {
        frame = new JFrame(" Social Media Post Scheduler");
        frame.setSize(450, 400);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("Social Media Post Scheduler", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBounds(50, 20, 340, 30);
        frame.add(title);

        JLabel contentLabel = new JLabel("Post Content:");
        contentLabel.setBounds(50, 70, 150, 25);
        frame.add(contentLabel);

        contentField = new JTextField();
        contentField.setBounds(180, 70, 200, 25);
        frame.add(contentField);

        JLabel imageLabel = new JLabel("Image (optional):");
        imageLabel.setBounds(50, 110, 150, 25);
        frame.add(imageLabel);

        imageField = new JTextField();
        imageField.setBounds(180, 110, 200, 25);
        frame.add(imageField);

        JButton browseBtn = new JButton("Browse");
        browseBtn.setBounds(180, 140, 100, 25);
        frame.add(browseBtn);

        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                imageField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        JLabel platformLabel = new JLabel("Select Platform:");
        platformLabel.setBounds(50, 180, 150, 25);
        frame.add(platformLabel);

        String[] platforms = {"Facebook", "Twitter", "Instagram"};
        platformBox = new JComboBox<>(platforms);
        platformBox.setBounds(180, 180, 200, 25);
        frame.add(platformBox);

        JLabel timeLabel = new JLabel("Schedule Time (yyyy-MM-dd HH:mm):");
        timeLabel.setBounds(50, 220, 250, 25);
        frame.add(timeLabel);

        timeField = new JTextField();
        timeField.setBounds(50, 250, 330, 25);
        frame.add(timeField);

        JButton scheduleBtn = new JButton("Schedule Post");
        scheduleBtn.setBounds(150, 290, 150, 35);
        frame.add(scheduleBtn);

        scheduleBtn.addActionListener(e -> schedulePost());

        frame.setVisible(true);
    }

    private void schedulePost() {
        try {
            String content = contentField.getText().trim();
            String image = imageField.getText().trim();
            String platform = Objects.requireNonNull(platformBox.getSelectedItem()).toString();
            String timeInput = timeField.getText().trim();

            if (content.isEmpty() || timeInput.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter content and time!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime scheduleTime = LocalDateTime.parse(timeInput, fmt);

            Post post = new Post(content, image, scheduleTime, platform);
            Scheduler scheduler = new Scheduler(post);
            scheduler.start();

            JOptionPane.showMessageDialog(frame,
                    " Post scheduled for " + platform + "\n Time: " + scheduleTime,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
