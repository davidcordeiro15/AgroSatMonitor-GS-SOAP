package com.agrosatmonitor.soap.enums;

public enum NivelRisco {
    BAIXO(1),
    MEDIO(2),
    ALTO(3),
    CRITICO(4);

    private final int codigo;

    NivelRisco(int codigo) { this.codigo = codigo; }

    public int getCodigo() { return codigo; }

    public static NivelRisco fromCodigo(int codigo) {
        for (NivelRisco n : values()) {
            if (n.codigo == codigo) return n;
        }
        throw new IllegalArgumentException("NivelRisco inválido: " + codigo);
    }
}
