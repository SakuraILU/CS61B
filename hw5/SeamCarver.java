import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
    private Picture picture;

    public SeamCarver(Picture picture) {
        this.picture = picture;
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        Color lcolor = picture.get(Math.floorMod(x - 1, width()), y);
        Color rcolor = picture.get(Math.floorMod(x + 1, width()), y);
        Color ucolor = picture.get(x, Math.floorMod(y - 1, height()));
        Color bcolor = picture.get(x, Math.floorMod(y + 1, height()));

        double energyX = Math.pow(rcolor.getRed() - lcolor.getRed(), 2)
                + Math.pow(rcolor.getGreen() - lcolor.getGreen(), 2)
                + Math.pow(rcolor.getBlue() - lcolor.getBlue(), 2);

        double energyY = Math.pow(ucolor.getRed() - bcolor.getRed(), 2)
                + Math.pow(ucolor.getGreen() - bcolor.getGreen(), 2)
                + Math.pow(ucolor.getBlue() - bcolor.getBlue(), 2);

        return energyX + energyY;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        Picture pictureT = new Picture(height(), width());

        for (int y = 0; y < pictureT.height(); y++) {
            for (int x = 0; x < pictureT.width(); x++) {
                pictureT.set(x, y, picture.get(y, x));
            }
        }

        SeamCarver sm = new SeamCarver(pictureT);
        return sm.findVerticalSeam();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] energyTo = new double[height()][width()];
        int[][] prevNode = new int[height()][width()];

        for (int x = 0; x < width(); x++) {
            energyTo[0][x] = energy(x, 0);
            prevNode[0][x] = -1;
        }

        for (int y = 1; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                double energyToLu = (x > 0) ? energyTo[y - 1][x - 1] : Double.MAX_VALUE;
                double energyToU = energyTo[y - 1][x];
                double energyToRu = (x < width() - 1) ? energyTo[y - 1][x + 1] : Double.MAX_VALUE;

                if (energyToLu <= energyToU && energyToLu <= energyToRu) {
                    energyTo[y][x] = energy(x, y) + energyToLu;
                    prevNode[y][x] = x - 1;
                } else if (energyToU <= energyToRu) {
                    energyTo[y][x] = energy(x, y) + energyToU;
                    prevNode[y][x] = x;
                } else {
                    energyTo[y][x] = energy(x, y) + energyToRu;
                    prevNode[y][x] = x + 1;
                }
            }
        }

        int minEnergyToX = 0;
        for (int x = 0; x < width(); x++) {
            if (energyTo[height() - 1][x] < energyTo[height() - 1][minEnergyToX]) {
                minEnergyToX = x;
            }
        }

        int[] seam = new int[height()];
        int node = minEnergyToX;
        for (int y = height() - 1; y >= 0; y--) {
            seam[y] = node;
            node = prevNode[y][node];
        }

        return seam;
    }

    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        picture = SeamRemover.removeHorizontalSeam(picture, seam);
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        picture = SeamRemover.removeVerticalSeam(picture, seam);
    }
}
