import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PelatihanGUI extends JFrame {
    private JTextField tfNoPendaftaran, tfNama, tfAlamat;
    private JComboBox<String> cbNoPendaftaran;
    private JButton btnCreate, btnRead, btnUpdate, btnDelete;

    public PelatihanGUI() {
        setTitle("Pelatihan Siswa");
        setSize(1650, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tfNoPendaftaran = new JTextField(5);
        tfNama = new JTextField(30);
        tfAlamat = new JTextField(40);
        cbNoPendaftaran = new JComboBox<>();
        btnCreate = new JButton("Create");
        btnRead = new JButton("Read");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");

        setLayout(new GridLayout(10, 2));
        add(new JLabel("No Pendaftaran:"));
        add(tfNoPendaftaran);
        add(new JLabel("Nama:"));
        add(tfNama);
        add(new JLabel("Alamat:"));
        add(tfAlamat);
        add(new JLabel("No Pendaftaran (ComboBox):"));
        add(cbNoPendaftaran);
        add(btnCreate);
        add(btnRead);
        add(btnUpdate);
        add(btnDelete);

        loadComboBox();

        btnCreate.addActionListener(e -> createRecord());
        btnRead.addActionListener(e -> readRecord());
        btnUpdate.addActionListener(e -> updateRecord());
        btnDelete.addActionListener(e -> deleteRecord());

        setVisible(true);
    }

    private void loadComboBox() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT no_pendaftaran FROM siswa";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            cbNoPendaftaran.removeAllItems();
            while (rs.next()) {
                String noPendaftaran = rs.getString("no_pendaftaran");
                cbNoPendaftaran.addItem(noPendaftaran);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        tfNoPendaftaran.setText("");
        tfNama.setText("");
        tfAlamat.setText("");
    }

    private void createRecord() {
        String noPendaftaran = tfNoPendaftaran.getText();
        String nama = tfNama.getText();
        String alamat = tfAlamat.getText();

        if (noPendaftaran.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "isi semua kolom!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertSiswa = "INSERT INTO siswa (no_pendaftaran, nama, alamat) VALUES (?, ?, ?)";
            PreparedStatement psSiswa = conn.prepareStatement(insertSiswa);
            psSiswa.setString(1, noPendaftaran);
            psSiswa.setString(2, nama);
            psSiswa.setString(3, alamat);
            psSiswa.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil tersimpan", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadComboBox();
            resetFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void readRecord() {
        String noPendaftaran = (String) cbNoPendaftaran.getSelectedItem();
        if (noPendaftaran == null || noPendaftaran.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih Nomor Pendaftaran!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT no_pendaftaran, nama, alamat FROM siswa WHERE no_pendaftaran = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, noPendaftaran);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfNoPendaftaran.setText(rs.getString("no_pendaftaran"));
                tfNama.setText(rs.getString("nama"));
                tfAlamat.setText(rs.getString("alamat"));
            } else {
                JOptionPane.showMessageDialog(this, "Tidak ada Data", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRecord() {
        String noPendaftaran = tfNoPendaftaran.getText();
        String nama = tfNama.getText();
        String alamat = tfAlamat.getText();

        if (noPendaftaran.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "isi semua kolom!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String updateSiswa = "UPDATE siswa SET nama = ?, alamat = ? WHERE no_pendaftaran = ?";
            PreparedStatement psSiswa = conn.prepareStatement(updateSiswa);
            psSiswa.setString(1, nama);
            psSiswa.setString(2, alamat);
            psSiswa.setString(3, noPendaftaran);
            psSiswa.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil tersimpan", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadComboBox();
            resetFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        String noPendaftaran = (String) cbNoPendaftaran.getSelectedItem();
        if (noPendaftaran == null || noPendaftaran.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih Nomor Pendaftaran!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String deleteSiswa = "DELETE FROM siswa WHERE no_pendaftaran = ?";
            PreparedStatement psSiswa = conn.prepareStatement(deleteSiswa);
            psSiswa.setString(1, noPendaftaran);
            psSiswa.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil Dihapus", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadComboBox();
            resetFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PelatihanGUI::new);
    }
}

class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/pelatihan";
        String username = "root";
        String password = "";
        return DriverManager.getConnection(url, username, password);
    }
}
