package ru.bpc.billing.util.poi;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * User: Krainov
 * Date: 13.11.13
 * Time: 17:06
 */
public class CellStyleDescriptor {

    private String dataFormat;
    private Short fontWeight;
    private Short backgroundColor;
    //private boolean borders;
    private Borders borders;
    private boolean wrapText;

//    public CellStyleDescriptor(String dataFormat, boolean borders) {
//        this.dataFormat = dataFormat;
//        this.borders = borders;
//    }
    public CellStyleDescriptor(String dataFormat, Borders borders) {
        this.dataFormat = dataFormat;
        this.borders = borders;
    }
    public CellStyleDescriptor(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public CellStyleDescriptor() {
    }

    public CellStyleDescriptor withBorders() {
        //this.borders = true;
        this.borders = new Borders();
        return this;
    }

    public CellStyleDescriptor withBorders(Borders borders) {
        //this.borders = true;
        this.borders = borders;
        return this;
    }

    public CellStyleDescriptor withBoldFont() {
        this.fontWeight = Font.BOLDWEIGHT_BOLD;
        return this;
    }

    public CellStyleDescriptor withBackgroundColor(short color) {
        this.backgroundColor = color;
        return this;
    }

    public CellStyleDescriptor wrapText() {
        this.wrapText = true;
        return this;
    }


    public CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font fontCell = workbook.createFont();
        fontCell.setFontName("Verdana");
        fontCell.setFontHeightInPoints(new Short("8"));
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setFont(fontCell);
        if ( null != borders) {
            if ( borders.bottom ) style.setBorderBottom(borders.size);
            if ( borders.left ) style.setBorderLeft(borders.size);
            if ( borders.right ) style.setBorderRight(borders.size);
            if ( borders.top ) style.setBorderTop(borders.size);
            style.setBorderBottom(CellStyle.BORDER_THIN);
            style.setBorderTop(CellStyle.BORDER_THIN);
        }
        if (dataFormat != null) {
            style.setDataFormat(workbook.createDataFormat().getFormat(dataFormat));
        }
//        if (fontWeight != null) {
//            Font font = workbook.createFont();
//            font.setBoldweight(fontWeight);
//            style.setFont(font);
//        }
        if (backgroundColor != null) {
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(backgroundColor);
        }
        style.setWrapText(wrapText);
        return style;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellStyleDescriptor that = (CellStyleDescriptor) o;

        if (wrapText != that.wrapText) return false;
        if (backgroundColor != null ? !backgroundColor.equals(that.backgroundColor) : that.backgroundColor != null)
            return false;
        if (borders != null ? !borders.equals(that.borders) : that.borders != null) return false;
        if (dataFormat != null ? !dataFormat.equals(that.dataFormat) : that.dataFormat != null) return false;
        if (fontWeight != null ? !fontWeight.equals(that.fontWeight) : that.fontWeight != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dataFormat != null ? dataFormat.hashCode() : 0;
        result = 31 * result + (fontWeight != null ? fontWeight.hashCode() : 0);
        result = 31 * result + (backgroundColor != null ? backgroundColor.hashCode() : 0);
        result = 31 * result + (borders != null ? borders.hashCode() : 0);
        result = 31 * result + (wrapText ? 1 : 0);
        return result;
    }

    public static class Borders {
        private boolean bottom;
        private boolean left;
        private boolean right;
        private boolean top;
        private short size = CellStyle.BORDER_MEDIUM;
        public Borders bottom() {bottom=true;return this;}
        public Borders left() {left=true;return this;}
        public Borders right() {right=true;return this;}
        public Borders top() {top=true;return this;}
        public Borders size(short size) {this.size=size;return this;}

        public boolean isBottom() {
            return bottom;
        }

        public boolean isLeft() {
            return left;
        }

        public boolean isRight() {
            return right;
        }

        public boolean isTop() {
            return top;
        }

        public short getSize() {
            return size;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Borders borders = (Borders) o;

            if (bottom != borders.bottom) return false;
            if (left != borders.left) return false;
            if (right != borders.right) return false;
            if (size != borders.size) return false;
            if (top != borders.top) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (bottom ? 1 : 0);
            result = 31 * result + (left ? 1 : 0);
            result = 31 * result + (right ? 1 : 0);
            result = 31 * result + (top ? 1 : 0);
            result = 31 * result + (int) size;
            return result;
        }
    }
}
