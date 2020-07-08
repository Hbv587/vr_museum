package be.kuleuven.softdev.cbhuo.vr_museum;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class ImageSplitter {

    public static List<ImagePiece> split(Bitmap bitmap, int xIndex, int yIndex, List<ImagePiece> pieces) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / 2;
        int pieceHeight = height / 2;

        ImagePiece piece = new ImagePiece();
        piece.index = 2 * yIndex + xIndex;
        int xValue = xIndex * pieceWidth;
        int yValue = yIndex * pieceHeight;
        piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
                pieceWidth, pieceHeight);
        pieces.set(piece.index, piece);

        return pieces;
    }

    public static List<ImagePiece> splitInit(Bitmap bitmap, int xPiece, int yPiece) {

        List<ImagePiece> pieces = new ArrayList<ImagePiece>(2 * 2);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / 2;
        int pieceHeight = height / 2;
        for (int i = 0; i < yPiece; i++) {
            for (int j = 0; j < xPiece; j++) {
                ImagePiece piece = new ImagePiece();
                piece.index = j + i * xPiece;
                int xValue = j * pieceWidth;
                int yValue = i * pieceHeight;
                piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
                        pieceWidth, pieceHeight);
                pieces.add(piece);
            }
        }

        return pieces;
    }

}
