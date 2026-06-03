package com.agrosatmonitor.soap.enums;

public enum NivelSaudeVegetacao {
    CRITICA(1),
    BAIXA(2),
    MODERADA(3),
    BOA(4),
    EXCELENTE(5);

    private final int codigo;

    NivelSaudeVegetacao(int codigo) { this.codigo = codigo; }

    public int getCodigo() { return codigo; }

    public static NivelSaudeVegetacao fromCodigo(int codigo) {
        for (NivelSaudeVegetacao n : values()) {
            if (n.codigo == codigo) return n;
        }
        throw new IllegalArgumentException("NivelSaudeVegetacao inválido: " + codigo);
    }
}
