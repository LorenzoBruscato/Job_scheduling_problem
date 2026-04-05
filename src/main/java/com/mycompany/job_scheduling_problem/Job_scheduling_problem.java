package com.mycompany.job_scheduling_problem;

import java.util.*;

public class Job_scheduling_problem {
	List<Elemento> populacao = new ArrayList<Elemento>();
	List<Integer> selecionados = new ArrayList<Integer>();
	private Random r = new Random();
	final double ideal = 45.0; // Preco ideal em reais
	final int POPULACAO = 50; // Populacao maxima
	final int SELECAO = 20; // Quantidade que pode exceder, nos cruzamentos.
	static final int EPOCAS = 100;
	static final int MUTANTES = 20;
	static final boolean MELHORES = true;
	static final boolean PIORES = false;
	static boolean DEBUG = true;

	public void criaPopulacao() {
		for (int i = 0; i < POPULACAO; i++) {
			populacao.add(new Elemento());
		}
	}

	public void ordenaPopulacao() {
		recalculaPontuacao();
		for (int a = 0; a < populacao.size() - 1; a++) {
			for (int b = a + 1; b > 0; b--) {
				Elemento e1 = (Elemento) populacao.get(b - 1);
				Elemento e2 = (Elemento) populacao.get(b);
				double v1 = e1.pontuacao - ideal;
				double v2 = e2.pontuacao - ideal;
				if (v1 < 0) {
					// Calcula o modulo
					v1 *= -1;
				}
				if (v2 < 0) {
					// Calcula o modulo
					v2 *= -1;
				}
				// Inverte
				if (v1 < v2) {
					populacao.remove(b - 1);
					populacao.remove(b - 1);
					populacao.add(b - 1, e2);
					populacao.add(b, e1);
				}
			}
		}
	}

	// os elementos selecionados podem ser os mais fortes ou os mais fracos
	void selecao(int quantidade, boolean fortes) {
		selecionados.clear();
		ordenaPopulacao();
		// Criando a roleta
		Vector<Integer> roleta = new Vector<Integer>();
		int peso = 1;
		if (!fortes) {
			peso = 10;
		}
		int cont = 0;
		for (int i = 0; i < populacao.size(); i++) {
			for (int j = 0; j < peso; j++) {
				Integer aux = i;
				roleta.add(aux);
			}
			if (cont > 4) {
				if (fortes) {
					peso++;
				} else {
					peso--;
				}
				cont = 0;
			} else {
				cont++;
			}
		}
		// Seleciona os elementos na roleta;
		for (int i = 0; i < SELECAO; i++) {
			// pega um numero aleatorio na roleta
			int escolhido = r.nextInt(roleta.size());
			// pega o indice na roleta
			Integer aux = (Integer) roleta.get(escolhido);
			// pega o elemento com seu indice
			Elemento e = (Elemento) populacao.get(aux.intValue());

			if (!e.selecionado) {
				// seta o elemento como selecionado
				e.selecionado = true;
				// insere seu indice no vetor de selecionados
				selecionados.add(aux);
			} else {
				i--;
			}
		}
		dbgln("Selecionados = " + selecionados.size());
	}

	void cruzamento() {
		while (selecionados.size() > 0) {
			// Pega o primeiro elemento da lista de selecionados
			Integer aux1 = (Integer) selecionados.get(0);
			Elemento e1 = (Elemento) populacao.get(aux1.intValue());
			e1.selecionado = false;
			selecionados.remove(0);

			// Pega o segundo elemento da lista de selecionados
			Integer aux2 = (Integer) selecionados.get(0);
			Elemento e2 = (Elemento) populacao.get(aux2.intValue());
			e2.selecionado = false;
			selecionados.remove(0);

			// cruza, criando dois filhos com as informacoes trocadas e
			// insere-os na populacao
			Elemento f1 = new Elemento(e1);
			Elemento f2 = new Elemento(e2);
			int idx1 = r.nextInt(5);
			int idx2 = r.nextInt(5);
			int troca = f1.elemento[idx1];
			f1.elemento[idx1] = f2.elemento[idx1];
			f2.elemento[idx1] = troca;
			if (idx1 != idx2) {
				troca = f1.elemento[idx2];
				f1.elemento[idx2] = f2.elemento[idx2];
				f2.elemento[idx2] = troca;
			}
			populacao.add(f1);
			populacao.add(f2);
			dbg("Cruzando os elementos " + idx1 + " e " + idx2);
			dbg(" dos individuos " + aux1.intValue());
			dbgln(" e " + aux2.intValue() + "---------------");
			e1.mostrarInformacoes();
			e2.mostrarInformacoes();
			f1.calculaPontuacao();
			f1.mostrarInformacoes();
			f2.calculaPontuacao();
			f2.mostrarInformacoes();
		}
		ordenaPopulacao();
	}

	void mutacao() {
		// Pode mutar ate 4 elementos
		int qtd = r.nextInt(MUTANTES + 1);
		for (int i = qtd; i > 0; i--) {
			// Escolhe um elemento aleatoriamente
			int j = r.nextInt(populacao.size());
			Elemento e = (Elemento) populacao.get(i);
			// Escolhe aleatoriamente um de seus elementos
			int k = r.nextInt(5);
			dbgln("Mutando elemento " + k + " do individulo " + j);
			if (DEBUG) {
				e.mostrarInformacoes();
			}
			// Muda sua informacao aleatoriamente
			e.elemento[k] = r.nextInt(10) + 1;
			e.calculaPontuacao();
			if (DEBUG) {
				e.mostrarInformacoes();
			}
		}
		ordenaPopulacao();
	}

	void matar() {
		boolean matando = true;
		int i = 0;
		while (matando) {
			Elemento e = (Elemento) populacao.get(i);
			if (e.selecionado) {
				populacao.remove(i);
				i--;
			}
			i++;
			if (i >= populacao.size()) {
				matando = false;
			}
		}
		selecionados.clear();
	}

	void recalculaPontuacao() {
		for (int i = 0; i < populacao.size(); i++) {
			Elemento e = (Elemento) populacao.get(i);
			e.calculaPontuacao();
		}
	}

	void mostrarResultado() {
		System.out.println("\n\nResultado do algoritmo");
		for (int i = 0; i < POPULACAO; i++) {
			Elemento e = (Elemento) populacao.get(populacao.size() - 1 - i);
			System.out.print("" + (i + 1) + "o: ");
			e.mostrarInformacoes();
		}
	}

	public static void dbg(String s) {
		if (DEBUG) {
			System.out.print(s);
		}
	}

	public static void dbgln(String s) {
		if (DEBUG)
			System.out.println(s);
	}

	public static void main(String[] arguments) {
		Job_scheduling_problem g = new Job_scheduling_problem();
		dbgln("Criando Populacao");
		g.criaPopulacao();
		for (int i = 0; i < EPOCAS; i++) {
			dbgln("**** EPOCA " + (i + 1) + " ****");
			dbgln("Populacao = " + g.populacao.size());
			dbgln("Selecionando os 20 melhores");
			g.selecao(20, MELHORES);
			dbgln("Cruzamento");
			g.cruzamento();
			dbgln("Populacao = " + g.populacao.size());
			dbgln("Selecionando os 20 piores");
			g.selecao(20, PIORES);
			dbgln("Matando");
			g.matar();
			dbgln("Populacao = " + g.populacao.size());
			dbgln("Mutacao");
			g.mutacao();
		}
		g.mostrarResultado();
	}
}

/**************************************************************
 * Caracteristicas dos elementos (total * 30dias) 0 - Lampadas - 11-20h/dia a
 * 100w/h - 1-2k/dia 1 - Chuveiro - 0.1-0.6h/dia a 5kw/h - 0.25-2.75k/dia 2 -
 * Televisao - 1-10h/dia a 100w/h - 0-1k/dia 3 - Computador - 1-10h/dia a 300w/h
 * - 0-3k/dia 4 - Video K7 - 1-6h/dia a 50w/h - 0-0.5k/dia
 **************************************************************/

class Elemento {
	int[] elemento = new int[5];
	double pontuacao = 0;
	boolean selecionado = false;
	final double kwh = 0.38;
	static Random r = new Random();

	Elemento() {
		for (int i = 0; i < 5; i++) {
			elemento[i] = r.nextInt(10) + 1;
		}
		calculaPontuacao();
		selecionado = false;
		if (Job_scheduling_problem.DEBUG) {
			mostrarInformacoes();
		}
	}

	Elemento(Elemento e) {
		for (int i = 0; i < 5; i++) {
			elemento[i] = e.elemento[i];
		}
		selecionado = false;
	}

	void mostrarInformacoes() {
		System.out.print("LAMPADAS: [" + (int) (elemento[0] + 9) + "h] ");
		System.out.print("CHUVEIRO: [" + (int) (((elemento[1] / 2) + 1) * 6)
				+ "min] ");
		System.out.print("TELEVISAO: [" + (int) (elemento[2]) + "h] ");
		System.out.print("COMPUTADOR: [" + (int) (elemento[3]) + "h] ");
		System.out.print("VIDEO K7: [" + (int) (elemento[4] / 2) + "h] ");
		System.out.println("VALOR: R$ (" + (int) pontuacao + ",00)");
	}

	public void calculaPontuacao() {
		pontuacao = (elemento[0] + 10) * 100; // Lampadas
		pontuacao += ((elemento[1] + 2) / 2) * 500; // Chuveiro
		pontuacao += elemento[2] * 100; // Televisao
		pontuacao += elemento[3] * 300; // Computador
		pontuacao += ((elemento[4] + 2) / 2) * 50; // Video k7
		pontuacao *= 30; // em 30 Dias
		pontuacao = (pontuacao / 1000) * kwh; // kwh = 0.38 - Valor em reais
	}
}