package br.com.oliveirawillian.file.importer.contract;

import br.com.oliveirawillian.data.dto.v1.PersonDTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileImporter {

    List<PersonDTO> importFile(InputStream inputStream) throws Exception;
}
