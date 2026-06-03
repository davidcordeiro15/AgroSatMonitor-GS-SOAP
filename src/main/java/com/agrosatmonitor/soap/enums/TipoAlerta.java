package com.agrosatmonitor.soap.enums;

public enum TipoAlerta {
    SECA(1),
    TEMPERATURA_EXTREMA(2),
    BAIXA_VEGETACAO(3),
    CHUVA_EXCESSIVA(4),
    VENTO_FORTE(5);

    private final int codigo;

    TipoAlerta(int codigo) { this.codigo = codigo; }

    public int getCodigo() { return codigo; }

    public static TipoAlerta fromCodigo(int codigo) {
        for (TipoAlerta t : values()) {
            if (t.codigo == codigo) return t;
        }
        throw new IllegalArgumentException("TipoAlerta inválido: " + codigo);
    }
}
