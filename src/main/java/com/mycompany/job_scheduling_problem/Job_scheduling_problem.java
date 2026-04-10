package com.mycompany.job_scheduling_problem;

import java.util.*;

public class Job_scheduling_problem {

    List<Elemento> populacao = new ArrayList<Elemento>();
    List<Integer> selecionados = new ArrayList<Integer>();
    private final Random r = new Random();
    final double ideal = 45.0; // Preco ideal em reais
    final int POPULACAO = 100; // Aumentado de 50 para 100
    final int SELECAO = 30; // Aumentado de 20 para 30
    static final int EPOCAS = 200; // Aumentado de 100 para 200
    static final int MUTANTES = 100; // Aumentado de 20 para 30
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

            // Escolhe duas posições aleatórias (0 a 4)
            int pos1 = r.nextInt(5);
            int pos2 = r.nextInt(5);

            // Guarda os valores do filho 1 na posição pos1
            int tempJob = f1.elemento[pos1];
            int tempVal = f1.valores[pos1];
            int tempTemp = f1.tempos[pos1];

            // Copia os valores do filho 2 (posição pos2) para o filho 1 (posição pos1)
            f1.elemento[pos1] = f2.elemento[pos2];
            f1.valores[pos1] = f2.valores[pos2];
            f1.tempos[pos1] = f2.tempos[pos2];

            // Copia os valores guardados do filho 1 para o filho 2 (posição pos2)
            f2.elemento[pos2] = tempJob;
            f2.valores[pos2] = tempVal;
            f2.tempos[pos2] = tempTemp;

            //Adicionar a população
            populacao.add(f1);
            populacao.add(f2);

            dbg("Cruzando os elementos " + pos1 + " e " + pos2);
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
        for (int i = 0; i < qtd; i++) {
            // Escolhe um elemento aleatoriamente
            int j = r.nextInt(populacao.size());
            Elemento e = (Elemento) populacao.get(j);
            // Escolhe aleatoriamente um de seus elementos
            int pos1 = r.nextInt(5);
            dbgln("Mutando elemento " + j + " - trocando posições " + pos1 + " e " + j);
            if (DEBUG) {
                e.mostrarInformacoes();
            }
            // Muda sua informacao aleatoriamente
            e.elemento[pos1] = r.nextInt(5);
            e.tempos[pos1] = r.nextInt(30) + 1;
            e.valores[pos1] = r.nextInt(100) + 1;
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
        if (DEBUG) {
            System.out.println(s);
        }
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

/**
 * Variáveis da classe Elemento:
 * - elemento[]: ordem de execução dos 5 jobs (0 a 4) 
 * - valores[]: lucro de cada job (R$ 25,45,35,55,15) 
 * - tempos[]: horas para executar cada job (4,12,6,9,3) 
 * - deadline[]: prazo máximo de cada job (15,40,25,35,20) 
 * - pontuacao: lucro total obtido 
 * - selecionado: indica se foi selecionado para cruzamento
 */
class Elemento {

    int[] elemento = {0, 1, 2, 3, 4};
    int[] valores = {25, 45, 35, 55, 15};
    int[] tempos = {4, 12, 6, 9, 3};
    int[] deadline = {15, 40, 25, 35, 20};

    int pontuacao = 0;
    boolean selecionado = false;
    static Random r = new Random();

    Elemento() {
        calculaPontuacao();
        selecionado = false;
        if (Job_scheduling_problem.DEBUG) {
            mostrarInformacoes();
        }
    }

    Elemento(Elemento e) {
        this.pontuacao = e.pontuacao;
        this.selecionado = false;
    }

    /**
     * Exibe todas as informações do escalonamento: 
     * - Ordem dos jobs 
     * - Tempos acumulados 
     * - Deadlines 
     * - Status de entrega (true = no prazo, false = atrasado)
     * - Lucro total
     */
    void mostrarInformacoes() {
        System.out.print("JOBS: ");
        for (int i = 0; i < 5; i++) {
            System.out.print("J" + (elemento[i] + 1));
            if (i < 4) {
                System.out.print(" > ");
            }
        }

        int tempoAtual = 0;
        int valorTotal = 0;

        // Exibe os tempos acumulados após cada job
        System.out.print(" | TEMPOS: ");
        for (int i = 0; i < 5; i++) {
            int jobId = elemento[i];
            tempoAtual += tempos[jobId];
            System.out.print(tempoAtual + "h");
            if (i < 4) {
                System.out.print("/");
            }
        }

        // Exibe os deadlines de cada job (na ordem executada)
        System.out.print(" | DEADLINES: ");
        for (int i = 0; i < 5; i++) {
            int jobId = elemento[i];
            System.out.print(deadline[jobId] + "h");
            if (i < 4) {
                System.out.print("/");
            }
        }

        // Exibe status de cada job (T = entregue no prazo, F = atrasado)
        System.out.print(" | STATUS: ");
        tempoAtual = 0;
        for (int i = 0; i < 5; i++) {
            int jobId = elemento[i];
            tempoAtual += tempos[jobId];
            if (tempoAtual <= deadline[jobId]) {
                System.out.print("T");
                valorTotal += valores[jobId];
            } else {
                System.out.print("F");
            }
            if (i < 4) {
                System.out.print("/");
            }
        }

        // Exibe o lucro total
        System.out.println(" | Profit: R$ (" + (int) valorTotal + ",00)");
        pontuacao = valorTotal;
    }

    /**
     * Calcula a pontuação (lucro total) do escalonamento atual
     *
     * Regra: Um job só gera lucro se seu tempo de término for menor ou igual ao
     * seu deadline
     */
    public void calculaPontuacao() {
        int total = 0;        // Acumula o lucro
        int tempoAtual = 0;   // Controla o tempo decorrido

        // Percorre os jobs na ordem definida
        for (int i = 0; i < 5; i++) {
            int jobId = elemento[i];           // Pega o ID do job atual
            tempoAtual += tempos[jobId];       // Soma o tempo do job

            // Se terminou antes do deadline, recebe o valor
            if (tempoAtual <= deadline[jobId]) {
                total += valores[jobId];
            }
            // Se atrasou, não recebe nada (job é ignorado)
        }

        pontuacao = total;  // Atualiza a pontuação
    }
}
