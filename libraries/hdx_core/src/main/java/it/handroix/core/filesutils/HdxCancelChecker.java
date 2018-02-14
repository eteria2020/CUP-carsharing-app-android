package it.handroix.core.filesutils;

/**
 * Classe astratta per controllare il cancel in lunghe operazioni su files.
 * Created by Andrea Richiardi on 26/06/14.
 */
public abstract class HdxCancelChecker {

    public abstract boolean isCancelled();
}
