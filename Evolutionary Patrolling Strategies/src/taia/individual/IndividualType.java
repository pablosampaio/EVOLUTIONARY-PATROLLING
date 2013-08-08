package taia.individual;

/*PAS: Nao entendi muito bem essa proposta de ter diferentes tipos
 * de individuo. Acho que o individuo tem que ser apenas a estrutura 
 * dados que guarda as informacoes + operações simples. Mas a informação 
 * detalhada de como fazer o mutate, como selecionar, como fazer crossover, 
 * etc., seria por meio de outras classes (ou enums) passados como 
 * parâmetros para os algoritmos. Se for criar um valor do enum para cada
 * possivel combinação dos parâmetros, esse enum pode ficar imenso e pouco
 * legivel (como dar nomes significativos que permitam diferenciar os
 * individuos?).
 */
public enum IndividualType {

	
	SIMPLEINDIVIDUAL;
	
}
