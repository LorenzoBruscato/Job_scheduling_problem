package com.mycompany.job_scheduling_problem;

import java.util.*;

public class Job_scheduling_problem {

    List<Elemento> populacao = new ArrayList<Elemento>();
    List<Integer> selecionados = new ArrayList<Integer>();
    private Random r = new Random();
    final int POPULACAO = 100;
    final int SELECAO = 30;
    static final int EPOCAS = 200;
    static final int MUTANTES = 100;
    static final boolean MELHORES = true;
    static final boolean PIORES = false;
    static boolean DEBUG = true;

    /**
     * Cria a população inicial com indivíduos aleatórios
     */
    public void criaPopulacao() {
        for (int i = 0; i < POPULACAO; i++) {
            populacao.add(new Elemento());
        }
    }

    /**
     * Ordena a população por pontuação (decrescente - melhores primeiro)
     */
    public void ordenaPopulacao() {
        recalculaPontuacao();

        // substitui bubble sort — MESMO RESULTADO, MENOS BUGS
        Collections.sort(populacao, (a, b) -> Double.compare(b.pontuacao, a.pontuacao));
    }

    /**
     * Seleciona indivíduos usando roleta viciada
     * @param quantidade Número de indivíduos a selecionar
     * @param fortes true = seleciona os melhores, false = seleciona os piores
     */
    void selecao(int quantidade, boolean fortes) {
        selecionados.clear();
        ordenaPopulacao();

        // Criando a roleta com pesos proporcionais à posição
        Vector<Integer> roleta = new Vector<Integer>();
        for (int i = 0; i < populacao.size(); i++) {
            int peso = fortes ? (populacao.size() - i) : (i + 1);
            for (int j = 0; j < peso; j++) {
                roleta.add(i);
            }
        }

        // Seleciona os elementos na roleta
        for (int i = 0; i < quantidade; i++) {
            int escolhido = r.nextInt(roleta.size());
            int idx = roleta.get(escolhido);
            Elemento e = populacao.get(idx);

            if (!e.selecionado) {
                e.selecionado = true;
                selecionados.add(idx);
            } else {
                i--;
            }
        }
        dbgln("Selecionados = " + selecionados.size());
    }

    /**
     * Realiza o cruzamento entre os indivíduos selecionados usando crossover OX
     */
    void cruzamento() {
        while (selecionados.size() > 0) {

            int aux1 = selecionados.remove(0);
            Elemento e1 = populacao.get(aux1);
            e1.selecionado = false;

            int aux2 = selecionados.remove(0);
            Elemento e2 = populacao.get(aux2);
            e2.selecionado = false;

            // Cria dois filhos usando crossover OX
            Elemento f1 = crossoverOX(e1, e2);
            Elemento f2 = crossoverOX(e2, e1);

            populacao.add(f1);
            populacao.add(f2);

            dbg("Cruzando individuos " + aux1);
            dbgln(" e " + aux2 + " ----------------");

            e1.mostrarInformacoes();
            e2.mostrarInformacoes();
            f1.calculaPontuacao();
            f1.mostrarInformacoes();
            f2.calculaPontuacao();
            f2.mostrarInformacoes();
        }

        ordenaPopulacao();
    }

    /**
     * Crossover Order Crossover (OX) - mantém a ordem relativa dos genes
     * @param p1 Primeiro pai
     * @param p2 Segundo pai
     * @return Filho gerado
     */
    Elemento crossoverOX(Elemento p1, Elemento p2) {
        int size = p1.elemento.length;

        Elemento filho = new Elemento(p1);
        Arrays.fill(filho.elemento, -1);

        boolean[] usado = new boolean[size];

        // Escolhe dois pontos de corte aleatórios
        int ini = r.nextInt(size);
        int fim = r.nextInt(size);
        if (ini > fim) {
            int t = ini;
            ini = fim;
            fim = t;
        }

        // Copia o segmento do primeiro pai
        for (int i = ini; i <= fim; i++) {
            filho.elemento[i] = p1.elemento[i];
            usado[p1.elemento[i]] = true;
        }

        // Preenche o restante com a ordem do segundo pai
        int idx = 0;
        for (int i = 0; i < size; i++) {
            if (!usado[p2.elemento[i]]) {
                while (filho.elemento[idx] != -1) {
                    idx++;
                }
                filho.elemento[idx] = p2.elemento[i];
            }
        }
        return filho;
    }

    /**
     * Aplica mutação por troca (swap) em indivíduos aleatórios
     */
    void mutacao() {
        int qtd = 10 + r.nextInt(MUTANTES + 1);

        for (int i = qtd; i > 0; i--) {

            int j = r.nextInt(populacao.size());
            Elemento e = populacao.get(j);

            int size = e.elemento.length;
            int k1 = r.nextInt(size);
            int k2 = r.nextInt(size);
            while (k1 == k2) {
                k2 = r.nextInt(size);
            }

            dbgln("Mutando individuo " + j + " (swap " + k1 + " <-> " + k2 + ")");

            if (DEBUG) {
                e.mostrarInformacoes();
            }

            // Troca duas posições
            int temp = e.elemento[k1];
            e.elemento[k1] = e.elemento[k2];
            e.elemento[k2] = temp;

            e.calculaPontuacao();

            if (DEBUG) {
                e.mostrarInformacoes();
            }
        }

        ordenaPopulacao();
    }

    /**
     * Remove os indivíduos marcados como selecionados (os piores)
     */
    void matar() {
        // elimina com segurança INDIVÍDUOS MARCADOS
        populacao.removeIf(e -> e.selecionado);
        selecionados.clear();
    }

    /**
     * Recalcula a pontuação de todos os indivíduos e reseta o flag selecionado
     */
    void recalculaPontuacao() {
        for (Elemento e : populacao) {
            e.calculaPontuacao();
            e.selecionado = false; // EVITA BUGS
        }
    }

    /**
     * Exibe os melhores resultados da população final
     */
    void mostrarResultado() {
        System.out.println("\n\nResultado do algoritmo");
        for (int i = 0; i < POPULACAO && i < populacao.size(); i++) {
            Elemento e = populacao.get(i);
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
            dbgln("Selecionando os 30 melhores");
            g.selecao(30, MELHORES);
            dbgln("Cruzamento");
            g.cruzamento();
            dbgln("Populacao = " + g.populacao.size());
            dbgln("Selecionando os 30 piores");
            g.selecao(30, PIORES);
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
 * Classe que representa um indivíduo (escalonamento de jobs)
 *
 * Variáveis:
 * - elemento[]: ordem de execução dos 5 jobs (0 a 4)
 * - duracao[]: tempo de duração de cada job (1, 10, 5, 15, 7 minutos)
 * - limite[]: prazo máximo de cada job (40, 22, 5, 37, 12 minutos)
 * - pontuacao: pontuação total (10 por job no prazo, -5 por atrasado)
 * - selecionado: indica se foi selecionado para cruzamento
 */
class Elemento {

    int[] elemento = new int[5];
    int[] duracao = {1, 10, 5, 15, 7};
    int[] limite = {40, 22, 5, 37, 12};
    double pontuacao = 0;
    boolean selecionado = false;
    static Random r = new Random();

    /**
     * Construtor padrão - cria um escalonamento aleatório
     */
    Elemento() {
        // Inicializa com ordem sequencial
        for (int i = 0; i < elemento.length; i++) {
            elemento[i] = i;
        }

        // Embaralha a ordem (Fisher-Yates)
        for (int i = elemento.length - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            int temp = elemento[i];
            elemento[i] = elemento[j];
            elemento[j] = temp;
        }
        calculaPontuacao();
        selecionado = false;

        if (Job_scheduling_problem.DEBUG) {
            mostrarInformacoes();
        }
    }

    /**
     * Construtor de cópia
     * @param e Elemento a ser copiado
     */
    Elemento(Elemento e) {
        for (int i = 0; i < elemento.length; i++) {
            elemento[i] = e.elemento[i];
        }
        selecionado = false;
    }

    /**
     * Exibe todas as informações do escalonamento:
     * - Tempos de término de cada job
     * - Pontuação total
     */
    void mostrarInformacoes() {
        int[] tempo = new int[5];

        tempo[0] = duracao[elemento[0]];
        for (int i = 1; i < tempo.length; i++) {
            tempo[i] = tempo[i - 1] + duracao[elemento[i]];
        }

        System.out.print("Job 1: [" + tempo[0] + "min] ");
        System.out.print("Job 2: [" + tempo[1] + "min] ");
        System.out.print("Job 3: [" + tempo[2] + "min] ");
        System.out.print("Job 4: [" + tempo[3] + "min] ");
        System.out.print("Job 5: [" + tempo[4] + "min] ");
        System.out.println("Pontuação : " + pontuacao);
    }

    /**
     * Calcula a pontuação do escalonamento atual
     *
     * Regra: Cada job que termina antes do limite ganha 10 pontos
     *        Cada job que atrasa perde 5 pontos
     */
    public void calculaPontuacao() {
        pontuacao = 0;

        int[] tempo = new int[5];

        tempo[0] = duracao[elemento[0]];
        pontuacao += (tempo[0] <= limite[elemento[0]]) ? 10 : -5;

        for (int i = 1; i < tempo.length; i++) {
            tempo[i] = tempo[i - 1] + duracao[elemento[i]];
            pontuacao += (tempo[i] <= limite[elemento[i]]) ? 10 : -5;
        }
    }
}