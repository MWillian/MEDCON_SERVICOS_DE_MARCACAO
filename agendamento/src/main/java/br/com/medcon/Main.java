package br.com.medcon;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        DayOfWeek dia = DayOfWeek.MONDAY;

        Locale brasil = Locale.of("pt", "BR");
        Locale eua = Locale.of("en", "US");

        String nomeBR = dia.getDisplayName(TextStyle.FULL, brasil).toUpperCase();
        String nomeUS = dia.getDisplayName(TextStyle.FULL, eua);

        System.out.println(nomeBR); // segunda-feira
        System.out.println(nomeUS); // Monday

    }
}