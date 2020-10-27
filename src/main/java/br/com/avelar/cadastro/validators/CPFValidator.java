// https://www.vivaolinux.com.br/script/Codigo-para-validar-CPF-e-CNPJ-otimizado

package br.com.avelar.cadastro.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPF, String> {

    private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

    @Override
    public void initialize(CPF constraintAnnotation) {

    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null) return false;

        cpf = cpf.strip().replace(".", "").replace("-", "");

        if (cpf.length() != 11) return false;

        Integer digito1 = calcularDigito(cpf.substring(0, 9), pesoCPF);
        Integer digito2 = calcularDigito(cpf.substring(0, 9) + digito1, pesoCPF);

        return cpf.equals(cpf.substring(0, 9) + digito1.toString() + digito2.toString());
    }

    private int calcularDigito(String str, int[] peso) {
        int soma = 0;

        for (int indice = str.length() - 1, digito; indice >= 0; indice--) {
            digito = Integer.parseInt(str.substring(indice,indice+1));
            soma += digito*peso[peso.length-str.length()+indice];
        }

        soma = 11 - soma % 11;

        return soma > 9 ? 0 : soma;
    }
}
