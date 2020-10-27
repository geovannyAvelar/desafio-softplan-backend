package br.com.avelar.cadastro.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor @AllArgsConstructor
public class ImageResolution {

    private Integer width;

    private Integer height;

    public ImageResolution scale(double percentage) {
        width = (int) (width * percentage);
        height = (int) (height * percentage);
        return this;
    }

}

