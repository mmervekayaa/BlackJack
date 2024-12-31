import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
    private class Kart {
        String deger;
        String tur;

        Kart(String deger, String tur) {
            this.deger = deger;
            this.tur = tur;
        }

        public String toString() {
            return deger + "-" + tur;
        }

        public int getDeger() {
            if ("AJQK".contains(deger)) { // A, J, Q, K kartları
                if (deger == "A") {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(deger); // 2-10 arasındaki kartlar
        }

        public boolean asMi() {
            return deger == "A";
        }

        public String getResimYolu() {
            return "./cards/" + toString() + ".png";
        }
    }

    ArrayList<Kart> deste;
    Random rastgele = new Random(); // desteyi karıştır

    // Kurpiyer
    Kart gizliKart;
    ArrayList<Kart> kurpiyerEl;
    int kurpiyerToplam;
    int kurpiyerAsSayisi;

    // Oyuncu
    ArrayList<Kart> oyuncuEl;
    int oyuncuToplam;
    int oyuncuAsSayisi;

    // Pencere
    int tahtaGenislik = 600;
    int tahtaYukseklik = tahtaGenislik;

    int kartGenislik = 110; // oran 1/1.4 olmalı
    int kartYukseklik = 154;

    JFrame pencere = new JFrame("Black Jack");
    JPanel oyunPaneli = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {
                // Gizli kartı çiz
                Image gizliKartResim = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (!bekleButon.isEnabled()) {
                    gizliKartResim = new ImageIcon(getClass().getResource(gizliKart.getResimYolu())).getImage();
                }
                g.drawImage(gizliKartResim, 20, 20, kartGenislik, kartYukseklik, null);

                // Kurpiyerin elini çiz
                for (int i = 0; i < kurpiyerEl.size(); i++) {
                    Kart kart = kurpiyerEl.get(i);
                    Image kartResim = new ImageIcon(getClass().getResource(kart.getResimYolu())).getImage();
                    g.drawImage(kartResim, kartGenislik + 25 + (kartGenislik + 5) * i, 20, kartGenislik, kartYukseklik, null);
                }

                // Oyuncunun elini çiz
                for (int i = 0; i < oyuncuEl.size(); i++) {
                    Kart kart = oyuncuEl.get(i);
                    Image kartResim = new ImageIcon(getClass().getResource(kart.getResimYolu())).getImage();
                    g.drawImage(kartResim, 20 + (kartGenislik + 5) * i, 320, kartGenislik, kartYukseklik, null);
                }

                if (!bekleButon.isEnabled()) {
                    kurpiyerToplam = kurpiyerAsAzalt();
                    oyuncuToplam = oyuncuAsAzalt();

                    String mesaj = "";
                    if (oyuncuToplam > 21) {
                        mesaj = "Kaybettiniz!";
                    } else if (kurpiyerToplam > 21) {
                        mesaj = "Kazandınız!";
                    } else if (oyuncuToplam == kurpiyerToplam) {
                        mesaj = "Berabere!";
                    } else if (oyuncuToplam > kurpiyerToplam) {
                        mesaj = "Kazandınız!";
                    } else {
                        mesaj = "Kaybettiniz!";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(mesaj, 220, 250);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    JPanel butonPaneli = new JPanel();
    JButton cekButon = new JButton("Çek");
    JButton bekleButon = new JButton("Bekle");

    BlackJack() {
        oyunuBaslat();

        pencere.setVisible(true);
        pencere.setSize(tahtaGenislik, tahtaYukseklik);
        pencere.setLocationRelativeTo(null);
        pencere.setResizable(false);
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        oyunPaneli.setLayout(new BorderLayout());
        oyunPaneli.setBackground(new Color(53, 101, 77));
        pencere.add(oyunPaneli);

        cekButon.setFocusable(false);
        butonPaneli.add(cekButon);
        bekleButon.setFocusable(false);
        butonPaneli.add(bekleButon);
        pencere.add(butonPaneli, BorderLayout.SOUTH);

        cekButon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Kart kart = deste.remove(deste.size() - 1);
                oyuncuToplam += kart.getDeger();
                oyuncuAsSayisi += kart.asMi() ? 1 : 0;
                oyuncuEl.add(kart);
                if (oyuncuAsAzalt() > 21) { // A + 2 + J --> 1 + 2 + J
                    cekButon.setEnabled(false);
                }
                oyunPaneli.repaint();
            }
        });

        bekleButon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cekButon.setEnabled(false);
                bekleButon.setEnabled(false);

                while (kurpiyerToplam < 17) {
                    Kart kart = deste.remove(deste.size() - 1);
                    kurpiyerToplam += kart.getDeger();
                    kurpiyerAsSayisi += kart.asMi() ? 1 : 0;
                    kurpiyerEl.add(kart);
                }
                oyunPaneli.repaint();
            }
        });

        oyunPaneli.repaint();
    }

    public void oyunuBaslat() {
        // Deste oluşturma
        desteOlustur();
        desteKaristir();

        // Kurpiyer başlangıcı
        kurpiyerEl = new ArrayList<Kart>();
        kurpiyerToplam = 0;
        kurpiyerAsSayisi = 0;

        gizliKart = deste.remove(deste.size() - 1);
        kurpiyerToplam += gizliKart.getDeger();
        kurpiyerAsSayisi += gizliKart.asMi() ? 1 : 0;

        Kart kart = deste.remove(deste.size() - 1);
        kurpiyerToplam += kart.getDeger();
        kurpiyerAsSayisi += kart.asMi() ? 1 : 0;
        kurpiyerEl.add(kart);

        // Oyuncu başlangıcı
        oyuncuEl = new ArrayList<Kart>();
        oyuncuToplam = 0;
        oyuncuAsSayisi = 0;

        for (int i = 0; i < 2; i++) {
            kart = deste.remove(deste.size() - 1);
            oyuncuToplam += kart.getDeger();
            oyuncuAsSayisi += kart.asMi() ? 1 : 0;
            oyuncuEl.add(kart);
        }
    }

    public void desteOlustur() {
        deste = new ArrayList<Kart>();
        String[] degerler = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] turler = {"C", "D", "H", "S"};

        for (int i = 0; i < turler.length; i++) {
            for (int j = 0; j < degerler.length; j++) {
                Kart kart = new Kart(degerler[j], turler[i]);
                deste.add(kart);
            }
        }
    }

    public void desteKaristir() {
        for (int i = 0; i < deste.size(); i++) {
            int j = rastgele.nextInt(deste.size());
            Kart mevcutKart = deste.get(i);
            Kart rastgeleKart = deste.get(j);
            deste.set(i, rastgeleKart);
            deste.set(j, mevcutKart);
        }
    }

    public int oyuncuAsAzalt() {
        while (oyuncuToplam > 21 && oyuncuAsSayisi > 0) {
            oyuncuToplam -= 10;
            oyuncuAsSayisi -= 1;
        }
        return oyuncuToplam;
    }

    public int kurpiyerAsAzalt() {
        while (kurpiyerToplam > 21 && kurpiyerAsSayisi > 0) {
            kurpiyerToplam -= 10;
            kurpiyerAsSayisi -= 1;
        }
        return kurpiyerToplam;
    }
}
