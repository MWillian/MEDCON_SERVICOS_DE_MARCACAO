package br.com.medcon.bo;

import br.com.medcon.dao.ProfissionalPostoDAO;

public class ProfissionalPostoBO {
    private final ProfissionalPostoDAO profissionalPostoDAO;

    public ProfissionalPostoBO(ProfissionalPostoDAO profissionalPostoDAO) {
        this.profissionalPostoDAO = profissionalPostoDAO;
    }
}
