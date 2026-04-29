-- [SQL-INGREDIENT-027] População da tabela de insumos com dados iniciais para padaria

INSERT INTO ingredients (
    name,
    description,
    purchase_price,
    stock_quantity,
    minimum_stock,
    unit_of_measure,
    active,
    created_at,
    updated_at,
    preferred_supplier_id
) VALUES
-- =====================================================
-- FARINHAS E MASSAS
-- =====================================================
('Farinha de Trigo Tipo 1', 'Farinha de trigo branca para pães e massas', 4.50, 500, 50, 'kg', true, now(), now(),1),
('Farinha de Trigo Integral', 'Farinha de trigo integral para pães saudáveis', 6.00, 200, 30, 'kg', true, now(), now(),1),
('Farinha de Milho Flocada', 'Farinha de milho para broa e pães de milho', 5.00, 150, 20, 'kg', true, now(), now(),1),
('Farinha de Centeio', 'Farinha de centeio para pães especiais', 8.00, 80, 15, 'kg', true, now(), now(),1),
('Amido de Milho', 'Amido de milho (maisena) para massas e bolos', 7.00, 100, 15, 'kg', true, now(), now(),1),
('Farinha de Arroz', 'Farinha de arroz para produtos sem glúten', 9.00, 60, 10, 'kg', true, now(), now(),1),

-- =====================================================
-- AÇÚCARES E ADOÇANTES
-- =====================================================
('Açúcar Cristal', 'Açúcar cristal branco para uso geral', 3.80, 300, 40, 'kg', true, now(), now(),1),
('Açúcar Refinado', 'Açúcar refinado branco', 4.00, 250, 35, 'kg', true, now(), now(),1),
('Açúcar Mascavo', 'Açúcar mascavo orgânico', 7.50, 100, 15, 'kg', true, now(), now(),1),
('Açúcar de Confeiteiro', 'Açúcar impalpável para confeitaria', 8.00, 80, 10, 'kg', true, now(), now(),1),
('Mel', 'Mel puro de abelhas', 25.00, 40, 10, 'kg', true, now(), now(),1),
('Melado de Cana', 'Melado de cana para pães e bolos', 12.00, 50, 8, 'kg', true, now(), now(),1),

-- =====================================================
-- FERMENTOS E LEVEDURAS
-- =====================================================
('Fermento Biológico Seco', 'Fermento biológico seco para pães', 15.00, 50, 10, 'pacote (10g)', true, now(), now(),1),
('Fermento Biológico Fresco', 'Fermento biológico fresco em tabletes', 8.00, 30, 8, 'kg', true, now(), now(),1),
('Fermento Químico', 'Fermento químico em pó (royal)', 12.00, 80, 15, 'kg', true, now(), now(),1),

-- =====================================================
-- OVOS E LATICÍNIOS
-- =====================================================
('Ovos Brancos', 'Ovos branco grandes', 15.00, 500, 60, 'dúzia', true, now(), now(),1),
('Ovos Vermelhos', 'Ovos vermelhos caipiras', 18.00, 300, 40, 'dúzia', true, now(), now(),1),
('Leite Integral', 'Leite integral pasteurizado', 5.00, 200, 30, 'litro', true, now(), now(),1),
('Leite Desnatado', 'Leite desnatado', 5.00, 150, 25, 'litro', true, now(), now(),1),
('Leite em Pó', 'Leite em pó integral', 25.00, 80, 15, 'kg', true, now(), now(),1),
('Manteiga sem Sal', 'Manteiga sem sal para confeitaria', 35.00, 100, 15, 'kg', true, now(), now(),1),
('Manteiga com Sal', 'Manteiga com sal para uso geral', 32.00, 80, 12, 'kg', true, now(), now(),1),
('Margarina', 'Margarina vegetal para massas', 12.00, 100, 20, 'kg', true, now(), now(),1),
('Creme de Leite', 'Creme de leite fresco', 18.00, 60, 10, 'litro', true, now(), now(),1),
('Queijo Mussarela', 'Queijo mussarela para salgados', 40.00, 80, 15, 'kg', true, now(), now(),1),
('Queijo Prato', 'Queijo prato para sanduíches', 42.00, 60, 10, 'kg', true, now(), now(),1),
('Queijo Minas', 'Queijo minas frescal', 35.00, 50, 10, 'kg', true, now(), now(),1),
('Requeijão', 'Requeijão cremoso', 25.00, 40, 8, 'kg', true, now(), now(),1),

-- =====================================================
-- ÓLEOS E GORDURAS
-- =====================================================
('Óleo de Soja', 'Óleo de soja para frituras', 7.00, 200, 30, 'litro', true, now(), now(),1),
('Óleo de Girassol', 'Óleo de girassol', 12.00, 100, 15, 'litro', true, now(), now(),1),
('Banha de Porco', 'Banha de porco para massas', 15.00, 60, 10, 'kg', true, now(), now(),1),
('Gordura Vegetal', 'Gordura vegetal hidrogenada', 10.00, 80, 15, 'kg', true, now(), now(),1),

-- =====================================================
-- RECHEADOS E COMPLEMENTOS SALGADOS
-- =====================================================
('Carne Moída', 'Carne bovina moída', 35.00, 150, 25, 'kg', true, now(), now(),1),
('Frango Desfiado', 'Frango desfiado temperado', 25.00, 120, 20, 'kg', true, now(), now(),1),
('Calabresa', 'Linguiça calabresa', 28.00, 80, 15, 'kg', true, now(), now(),1),
('Presunto', 'Presunto cozido fatiado', 30.00, 60, 10, 'kg', true, now(), now(),1),
('Peito de Peru', 'Peito de peru fatiado', 45.00, 40, 8, 'kg', true, now(), now(),1),
('Bacon', 'Bacon em cubos', 40.00, 50, 10, 'kg', true, now(), now(),1),
('Salsicha', 'Salsicha para cachorro-quente', 12.00, 100, 20, 'kg', true, now(), now(),1),

-- =====================================================
-- RECHEADOS E COMPLEMENTOS DOCES
-- =====================================================
('Chocolate em Pó', 'Chocolate em pó 50% cacau', 25.00, 80, 15, 'kg', true, now(), now(),1),
('Chocolate ao Leite', 'Chocolate ao leite derretível', 40.00, 60, 10, 'kg', true, now(), now(),1),
('Chocolate Branco', 'Chocolate branco derretível', 45.00, 40, 8, 'kg', true, now(), now(),1),
('Doce de Leite', 'Doce de leite pastoso', 18.00, 80, 15, 'kg', true, now(), now(),1),
('Goiabada', 'Goiabada cascão', 15.00, 60, 10, 'kg', true, now(), now(),1),
('Coco Ralado', 'Coco ralado seco', 20.00, 50, 10, 'kg', true, now(), now(),1),
('Creme de Avelã', 'Creme de avelã tipo Nutella', 50.00, 30, 5, 'kg', true, now(), now(),1),

-- =====================================================
-- FRUTAS (IN NATURA E PROCESSADAS)
-- =====================================================
('Banana', 'Banana nanica para vitaminas e bolos', 6.00, 100, 20, 'kg', true, now(), now(),1),
('Maçã', 'Maçã fuji', 8.00, 60, 10, 'kg', true, now(), now(),1),
('Morango', 'Morango fresco', 15.00, 40, 8, 'kg', true, now(), now(),1),
('Laranja', 'Laranja pera para suco', 5.00, 150, 30, 'kg', true, now(), now(),1),
('Limão', 'Limão taiti', 4.00, 80, 15, 'kg', true, now(), now(),1),
('Polpa de Frutas', 'Polpa de frutas congeladas (diversos sabores)', 12.00, 100, 20, 'kg', true, now(), now(),1),

-- =====================================================
-- BEBIDAS E INSUMOS LÍQUIDOS
-- =====================================================
('Café Torrado', 'Café torrado e moído', 35.00, 60, 10, 'kg', true, now(), now(),1),
('Chá Mate', 'Erva mate para chimarrão/tereré', 20.00, 40, 8, 'kg', true, now(), now(),1),
('Chá Preto', 'Chá preto em folhas', 25.00, 30, 5, 'kg', true, now(), now(),1),
('Extrato de Baunilha', 'Extrato natural de baunilha', 80.00, 10, 2, 'litro', true, now(), now(),1),
('Corante Alimentício', 'Corante alimentício diversos', 15.00, 20, 5, 'unidade', true, now(), now(),1),

-- =====================================================
-- EMBALAGENS
-- =====================================================
('Saco Plástico Pão', 'Saco plástico para embalar pão francês', 25.00, 1000, 100, 'pacote (100 un)', true, now(), now(),1),
('Saco Plástico Transparente', 'Saco plástico transparente pequeno', 15.00, 2000, 200, 'pacote (100 un)', true, now(), now(),1),
('Caixa de Papelão', 'Caixa de papelão para delivery', 3.00, 500, 50, 'unidade', true, now(), now(),1),
('Embalagem para Bolo', 'Embalagem plástica para bolo', 2.50, 300, 30, 'unidade', true, now(), now(),1),
('Guardanapo', 'Guardanapo de papel', 10.00, 500, 50, 'pacote (100 un)', true, now(), now(),1),
('Copo Descartável', 'Copo descartável 200ml', 20.00, 1000, 100, 'pacote (100 un)', true, now(), now(),1),
('Talher Descartável', 'Kit de talher descartável', 15.00, 800, 80, 'pacote (100 un)', true, now(), now(),1),

-- =====================================================
-- MATERIAIS DE LIMPEZA (indiretos)
-- =====================================================
('Detergente', 'Detergente líquido neutro', 5.00, 100, 20, 'litro', true, now(), now(),1),
('Desinfetante', 'Desinfetante cloro ativo', 6.00, 80, 15, 'litro', true, now(), now(),1),
('Luva Descartável', 'Luva descartável para manipulação', 30.00, 200, 20, 'pacote (100 pares)', true, now(), now(),1),
('Papel Toalha', 'Papel toalha para cozinha', 25.00, 150, 20, 'pacote', true, now(), now(),1);


