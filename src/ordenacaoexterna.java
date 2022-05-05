import java.io.*;

public class ordenacaoexterna {

  public static int pegarParteDecimalFloat(float num) {

    String n = String.valueOf(num);
    int numero1 = 0;
    for (int i = 0; i < n.length(); i++) {

      char a = n.charAt(i);

      if (a == '.') {
        i++;

        a = n.charAt(i);
        if (a != '0') {
          numero1 = Character.getNumericValue(a);
        }

        i = n.length();
      }

    }
    return numero1;
  }

  public static int qtdElementoArrayIndice(indice[] a) {
    // essa funcao pega a quantidade de elementos presente no array de objeto
    // indice, foi construida com o objetivo de gerar o número do elemento mais a
    // direita quando o vetor não está completo
    int contador = 0;

    for (int i = 0; i < a.length; i++) {

      if (a[i] != null) {
        contador++;
      }

    }
    return contador;
  }

  public static boolean corrigirArquivoIndice() {

    // 0_________1___"0"____2________3_________4
    // Para embaralhar e a ordenacao fazer sentido eu mudei a ordem de escrita,
    // então se escreve primeiros números impares e depois numeros pares, porém já
    // que sempre gera o indice par primeiro ele salva 13 posicao na frente da posi
    // 0
    // para deixar o espaço para o numero par, porém quando fica um número impar de
    // elementos como no exemplo acima, fica um espaço de 0 no meio do arquivo, essa
    // funcao copia o ultimo registro para esse espaço e deixa o 0 pro fim do
    // arquivo, porém ainda continua com o 0, e ela retorna um boolean para o método
    // de ordenacao, para ele saber, quando ele deve ignorar as ultimas 13 casas que
    // seria um registro zerado (quando for impar), e quando ele considera esses 13
    // bytes finais (quando o número de elementos no arquivo for par).
    boolean eImpar = false;
    // funcao tem objetivo de tirar o gap de 0 entre os registros
    try {

      RandomAccessFile arq = new RandomAccessFile("src/database/aindices.db", "rw");

      if (arq.length() > 13) {

        arq.seek(arq.length() - 24);

        long lerLong = arq.readLong();

        byte[] zerar = new byte[12];
        if (lerLong == 0) {

          indice indd = new indice();
          arq.seek(arq.length() - 13);
          indd.setIdIndice(arq.readShort());
          indd.setPosiIndice(arq.readLong());
          indd.setLapide(arq.readUTF());

          arq.seek(arq.length() - 26);

          arq.writeShort(indd.getIdIndice());
          arq.writeLong(indd.getPosiIndice());
          arq.writeUTF(indd.getLapide());

          arq.seek((arq.length() - 12));
          arq.write(zerar);
          eImpar = true;
        }

      }

      arq.close();

    } catch (Exception e) {
      System.out.println("Aconteceu um error ao corrigir o arquivo de dados: " + e.getMessage());
    }
    return eImpar;
  }

  public void ordenacaoExterna() {
    arquivocrud arqcru = new arquivocrud();
    int tamanhoCaminho = 10;
    long contador1 = 0;
    int contador2 = 0;
    int contador3 = 0;
    boolean ultimoSavearq1 = false;
    boolean ultimoSavearq2 = false;
    boolean ultimoSavearq3 = false;
    boolean ultimoSavearq4 = false;

    try {

      RandomAccessFile arq1 = new RandomAccessFile("src/database/arq1.db", "rw");
      RandomAccessFile arq2 = new RandomAccessFile("src/database/arq2.db", "rw");
      RandomAccessFile arq3 = new RandomAccessFile("src/database/arq3.db", "rw");
      RandomAccessFile arq4 = new RandomAccessFile("src/database/arq4.db", "rw");
      indice ic = new indice();
      indice ic2 = new indice();
      // pegar o arq com mais caminhos para ler todos
      long tamanhoArq1 = arq1.length();
      tamanhoArq1 = (long) tamanhoArq1 / 13;

      long tamanhoArq2 = arq2.length();

      tamanhoArq2 = (long) tamanhoArq2 / 13;

      float tamanhoPararExecucao = 0;
      int qtdExecucaoPrincipal = 0;
      int qtdExecucaoIncompleta = 0;
      if (tamanhoArq1 == tamanhoArq2) {
        tamanhoPararExecucao = (float) tamanhoArq1 / 10;
      } else {
        if (tamanhoArq1 > tamanhoArq2) {
          tamanhoPararExecucao = (float) tamanhoArq1 / 10;

          if (tamanhoArq2 != 0) {
            tamanhoPararExecucao += (float) tamanhoArq2 / 10;
          }

        } else {
          tamanhoPararExecucao = (float) tamanhoArq2 / 10;
          if (tamanhoArq1 != 0) {
            tamanhoPararExecucao += (float) tamanhoArq1 / 10;
          }

        }
      }

      qtdExecucaoPrincipal = (int) Math.ceil(tamanhoPararExecucao / 2);
      qtdExecucaoIncompleta = pegarParteDecimalFloat(tamanhoPararExecucao);

      long valor1 = 0;
      long valor2 = 0;
      int contadorPontarq1 = 0;
      int contadorPontarq2 = 0;
      boolean podeLerV1 = true;
      boolean podeLerV2 = true;

      while (contador3 < qtdExecucaoPrincipal) {// laco que muda os 2 arquivores leitores (1 e 2) para o arquivo
                                                // salvador (3 e 4)

        if ((contador3 % 2) == 0) {// vai ler de 1 e 2 e salvar em 3 e 4
          arqcru.deletaTudo(-1, -1, -1, 1, 1);
          if (contador3 != 0) {
            tamanhoCaminho += 10;
            qtdExecucaoPrincipal /= 2;
            arq1.seek(0);
            arq2.seek(0);
            arq3.seek(0);
            arq4.seek(0);
          }
          while (contador1 < qtdExecucaoPrincipal) {// laco que muda entre os arquivos salvadores

            contadorPontarq1 = 0;
            contadorPontarq2 = 0;
            contador2 = 0;

            if ((contador1 % 2) == 0) {// vai ler de 1 e 2 e salvar em 3

              while (contador2 < tamanhoCaminho) {

                if (podeLerV1) {
                  // leu o registro do 1 arquivo
                  ic.setIdIndice(arq1.readShort());
                  ic.setPosiIndice(arq1.readLong());
                  ic.setLapide(arq1.readUTF());
                  valor1 = ic.getIdIndice();

                }

                if (podeLerV2) {
                  // leu o registro do 2 arquivo

                  ic2.setIdIndice(arq2.readShort());
                  valor2 = ic2.getIdIndice();
                  ic2.setPosiIndice(arq2.readLong());
                  ic2.setLapide(arq2.readUTF());

                }

                // fazer a comparacao

                if (valor2 < valor1) {

                  arq3.writeShort(ic2.getIdIndice());
                  arq3.writeLong(ic2.getPosiIndice());
                  arq3.writeUTF(ic2.getLapide());
                  podeLerV1 = false;
                  podeLerV2 = true;
                  contadorPontarq2++;

                } else {
                  arq3.writeShort(ic.getIdIndice());
                  arq3.writeLong(ic.getPosiIndice());
                  arq3.writeUTF(ic.getLapide());
                  podeLerV1 = true;
                  podeLerV2 = false;
                  contadorPontarq1++;

                }

                contador2++;
              }

              if (contadorPontarq1 != tamanhoCaminho) {
                long tamArq3 = arq3.length();
                arq3.seek(tamArq3);
                while (contadorPontarq1 <= tamanhoCaminho - 1) {
                  arq3.writeShort(ic.getIdIndice());
                  arq3.writeLong(ic.getPosiIndice());
                  arq3.writeUTF(ic.getLapide());
                  contadorPontarq1++;
                  if (contadorPontarq1 < tamanhoCaminho) {
                    ic.setIdIndice(arq1.readShort());
                    ic.setPosiIndice(arq1.readLong());
                    ic.setLapide(arq1.readUTF());
                  }

                }

              } else if (contadorPontarq2 != tamanhoCaminho) {

                long tamArq3 = arq3.length();
                arq3.seek(tamArq3);
                while (contadorPontarq2 <= tamanhoCaminho - 1) {
                  arq3.writeShort(ic2.getIdIndice());
                  arq3.writeLong(ic2.getPosiIndice());
                  arq3.writeUTF(ic2.getLapide());
                  contadorPontarq2++;
                  if (contadorPontarq2 < tamanhoCaminho) {
                    ic2.setIdIndice(arq2.readShort());
                    ic2.setPosiIndice(arq2.readLong());
                    ic2.setLapide(arq2.readUTF());
                  }

                }
              }
              ultimoSavearq1 = false;
              ultimoSavearq2 = false;
              ultimoSavearq3 = true;
              ultimoSavearq4 = false;
              contador1++;
            } else {// vai ler de 1 e 2 e salvar em 4
              contador2 = 0;
              contadorPontarq1 = 0;
              contadorPontarq2 = 0;
              podeLerV1 = true;
              podeLerV2 = true;
              valor1 = 0;
              valor2 = 0;
              while (contador2 < tamanhoCaminho) {

                if (podeLerV1) {
                  // leu o registro do 1 arquivo
                  ic.setIdIndice(arq1.readShort());
                  ic.setPosiIndice(arq1.readLong());
                  ic.setLapide(arq1.readUTF());
                  valor1 = ic.getIdIndice();

                }

                if (podeLerV2) {
                  // leu o registro do 2 arquivo

                  ic2.setIdIndice(arq2.readShort());
                  valor2 = ic2.getIdIndice();
                  ic2.setPosiIndice(arq2.readLong());
                  ic2.setLapide(arq2.readUTF());

                }

                // fazer a comparacao

                if (valor2 < valor1) {

                  arq4.writeShort(ic2.getIdIndice());
                  arq4.writeLong(ic2.getPosiIndice());
                  arq4.writeUTF(ic2.getLapide());
                  podeLerV1 = false;
                  podeLerV2 = true;
                  contadorPontarq2++;

                } else {
                  arq4.writeShort(ic.getIdIndice());
                  arq4.writeLong(ic.getPosiIndice());
                  arq4.writeUTF(ic.getLapide());
                  podeLerV1 = true;
                  podeLerV2 = false;
                  contadorPontarq1++;

                }

                contador2++;
              }

              if (contadorPontarq1 != tamanhoCaminho) {
                long tamArq4 = arq4.length();
                arq4.seek(tamArq4);
                while (contadorPontarq1 <= tamanhoCaminho - 1) {
                  arq4.writeShort(ic.getIdIndice());
                  arq4.writeLong(ic.getPosiIndice());
                  arq4.writeUTF(ic.getLapide());
                  contadorPontarq1++;
                  if (contadorPontarq1 < tamanhoCaminho) {
                    ic.setIdIndice(arq1.readShort());
                    ic.setPosiIndice(arq1.readLong());
                    ic.setLapide(arq1.readUTF());
                  }

                }

              } else if (contadorPontarq2 != tamanhoCaminho) {

                long tamArq4 = arq4.length();
                arq4.seek(tamArq4);
                while (contadorPontarq2 <= tamanhoCaminho - 1) {
                  arq4.writeShort(ic2.getIdIndice());
                  arq4.writeLong(ic2.getPosiIndice());
                  arq4.writeUTF(ic2.getLapide());
                  contadorPontarq2++;
                  if (contadorPontarq2 < tamanhoCaminho) {
                    ic2.setIdIndice(arq2.readShort());
                    ic2.setPosiIndice(arq2.readLong());
                    ic2.setLapide(arq2.readUTF());
                  }

                }
              }
              ultimoSavearq1 = false;
              ultimoSavearq2 = false;
              ultimoSavearq3 = false;
              ultimoSavearq4 = true;
              contador1++;
            }
          }

        } else {// ler arquivo 3 e 4 e salvar em 1 e 2

          arqcru.deletaTudo(-1, 1, 1, -1, -1);

          tamanhoCaminho += 10;
          podeLerV1 = true;
          podeLerV2 = true;
          int contadorPontarq3 = 0;
          int contadorPontarq4 = 0;
          valor1 = 0;
          valor2 = 0;
          contador1 = 0;
          tamanhoPararExecucao = (int) Math.round(tamanhoPararExecucao / 2);
          while (contador1 < tamanhoPararExecucao) {// laco que muda os 2 arquivores leitores (3 e 4) para o
                                                    // arquivo
            // salvador (1 e 2)
            arq1.seek(0);
            arq2.seek(0);
            arq3.seek(0);
            arq4.seek(0);
            contadorPontarq3 = 0;
            contadorPontarq4 = 0;
            contador2 = 0;

            if ((contador1 % 2) == 0) {// vai ler de 3 e 4 e salvar em 1 e 2

              while (contador2 < tamanhoCaminho) {// laco que salva do arquivo 3 e 4 no arquivo 1

                if (podeLerV1) {
                  // leu o registro do 1 arquivo
                  ic.setIdIndice(arq3.readShort());
                  ic.setPosiIndice(arq3.readLong());
                  ic.setLapide(arq3.readUTF());
                  valor1 = ic.getIdIndice();

                }

                if (podeLerV2) {
                  // leu o registro do 2 arquivo

                  ic2.setIdIndice(arq4.readShort());
                  valor2 = ic2.getIdIndice();
                  ic2.setPosiIndice(arq4.readLong());
                  ic2.setLapide(arq4.readUTF());

                }

                // fazer a comparacao

                if (valor2 < valor1) {

                  arq1.writeShort(ic2.getIdIndice());
                  arq1.writeLong(ic2.getPosiIndice());
                  arq1.writeUTF(ic2.getLapide());
                  podeLerV1 = false;
                  podeLerV2 = true;
                  contadorPontarq4++;

                } else {
                  arq1.writeShort(ic.getIdIndice());
                  arq1.writeLong(ic.getPosiIndice());
                  arq1.writeUTF(ic.getLapide());
                  podeLerV1 = true;
                  podeLerV2 = false;
                  contadorPontarq3++;

                }

                contador2++;
              }

              if (contadorPontarq3 != tamanhoCaminho) {
                long tamArq1 = arq1.length();
                arq1.seek(tamArq1);
                while (contadorPontarq3 <= tamanhoCaminho - 1) {
                  arq1.writeShort(ic.getIdIndice());
                  arq1.writeLong(ic.getPosiIndice());
                  arq1.writeUTF(ic.getLapide());
                  contadorPontarq3++;
                  if (contadorPontarq3 < tamanhoCaminho) {
                    ic.setIdIndice(arq3.readShort());
                    ic.setPosiIndice(arq3.readLong());
                    ic.setLapide(arq3.readUTF());
                  }

                }

              } else if (contadorPontarq4 != tamanhoCaminho) {

                long tamArq1 = arq1.length();
                arq1.seek(tamArq1);
                while (contadorPontarq4 <= tamanhoCaminho - 1) {
                  arq1.writeShort(ic2.getIdIndice());
                  arq1.writeLong(ic2.getPosiIndice());
                  arq1.writeUTF(ic2.getLapide());
                  contadorPontarq4++;
                  if (contadorPontarq4 < tamanhoCaminho) {
                    ic2.setIdIndice(arq4.readShort());
                    ic2.setPosiIndice(arq4.readLong());
                    ic2.setLapide(arq4.readUTF());
                  }

                }
              }
              ultimoSavearq1 = true;
              ultimoSavearq2 = false;
              ultimoSavearq3 = false;
              ultimoSavearq4 = false;
              contador1++;
            } else {// vai ler de 3 e 4 e salvar em 4
              // pegar codigo acima e ler do arquivo 3 e 4 e salvar no 4.

              contadorPontarq3 = 0;
              contadorPontarq4 = 0;
              podeLerV1 = true;
              podeLerV2 = true;
              valor1 = 0;
              valor2 = 0;

              while (contador2 < tamanhoCaminho) {// laco que salva do arquivo 3 e 4 no arquivo 4

                if (podeLerV1) {
                  // leu o registro do 1 arquivo
                  ic.setIdIndice(arq3.readShort());
                  ic.setPosiIndice(arq3.readLong());
                  ic.setLapide(arq3.readUTF());
                  valor1 = ic.getIdIndice();

                }

                if (podeLerV2) {
                  // leu o registro do 2 arquivo

                  ic2.setIdIndice(arq4.readShort());
                  valor2 = ic2.getIdIndice();
                  ic2.setPosiIndice(arq4.readLong());
                  ic2.setLapide(arq4.readUTF());

                }

                // fazer a comparacao

                if (valor2 < valor1) {

                  arq2.writeShort(ic2.getIdIndice());
                  arq2.writeLong(ic2.getPosiIndice());
                  arq2.writeUTF(ic2.getLapide());
                  podeLerV1 = false;
                  podeLerV2 = true;
                  contadorPontarq4++;

                } else {
                  arq2.writeShort(ic.getIdIndice());
                  arq2.writeLong(ic.getPosiIndice());
                  arq2.writeUTF(ic.getLapide());
                  podeLerV1 = true;
                  podeLerV2 = false;
                  contadorPontarq3++;

                }

                contador2++;
              }

              if (contadorPontarq3 != tamanhoCaminho) {
                long tamArq2 = arq2.length();
                arq2.seek(tamArq2);
                while (contadorPontarq3 <= tamanhoCaminho - 1) {
                  arq2.writeShort(ic.getIdIndice());
                  arq2.writeLong(ic.getPosiIndice());
                  arq2.writeUTF(ic.getLapide());
                  contadorPontarq3++;
                  if (contadorPontarq3 < tamanhoCaminho) {
                    ic.setIdIndice(arq3.readShort());
                    ic.setPosiIndice(arq3.readLong());
                    ic.setLapide(arq3.readUTF());
                  }

                }

              } else if (contadorPontarq4 != tamanhoCaminho) {

                long tamArq2 = arq2.length();
                arq2.seek(tamArq2);
                while (contadorPontarq4 <= tamanhoCaminho - 1) {
                  arq2.writeShort(ic2.getIdIndice());
                  arq2.writeLong(ic2.getPosiIndice());
                  arq2.writeUTF(ic2.getLapide());
                  contadorPontarq4++;
                  if (contadorPontarq4 < tamanhoCaminho) {
                    ic2.setIdIndice(arq4.readShort());
                    ic2.setPosiIndice(arq4.readLong());
                    ic2.setLapide(arq4.readUTF());
                  }

                }
              }
              ultimoSavearq1 = false;
              ultimoSavearq2 = true;
              ultimoSavearq3 = false;
              ultimoSavearq4 = false;
              contador1++;
            }

          }

        }

        contador3++;
      }
      arq1.close();
      arq2.close();
      arq3.close();
      arq4.close();
    } catch (Exception e) {
      String error = e.getMessage();
      System.out.println("Erro: " + error);
    }

    // essa parte abaixo pega o ultimo arquivo que foi salvo algo na ordenação
    // externa e copia para o arquivo aindices para ser feito a pesquisa.
    try {
      RandomAccessFile arqIndice = new RandomAccessFile("src/database/aindices.db", "rw");
      if (ultimoSavearq1 == true) {

        RandomAccessFile arq1 = new RandomAccessFile("src/database/arq1.db", "rw");
        long tamanhoArq1 = arq1.length();
        int tam = (int) tamanhoArq1;
        byte[] ba;
        ba = new byte[tam];
        // copiarTodosOsBytesdoArq(ba, arq1, tam);
        arq1.read(ba);
        arqIndice.seek(0);
        arqIndice.write(ba);
        arq1.close();
      }

      if (ultimoSavearq2 == true) {

        RandomAccessFile arq2 = new RandomAccessFile("src/database/arq2.db", "rw");
        long tamanhoArq2 = arq2.length();
        int tam = (int) tamanhoArq2;
        byte[] ba;
        ba = new byte[tam];
        // copiarTodosOsBytesdoArq(ba, arq1, tam);
        arq2.read(ba);
        arqIndice.seek(0);
        arqIndice.write(ba);
        arq2.close();
      }

      if (ultimoSavearq3 == true) {

        RandomAccessFile arq3 = new RandomAccessFile("src/database/arq3.db", "rw");
        long tamanhoArq3 = arq3.length();
        int tam = (int) tamanhoArq3;
        byte[] ba;
        ba = new byte[tam];
        // copiarTodosOsBytesdoArq(ba, arq1, tam);
        arq3.read(ba);
        arqIndice.seek(0);
        arqIndice.write(ba);
        arq3.close();
      }

      if (ultimoSavearq4 == true) {

        RandomAccessFile arq4 = new RandomAccessFile("src/database/arq4.db", "rw");
        long tamanhoArq4 = arq4.length();
        int tam = (int) tamanhoArq4;
        byte[] ba;
        ba = new byte[tam];
        // copiarTodosOsBytesdoArq(ba, arq1, tam);
        arq4.read(ba);
        arqIndice.seek(0);
        arqIndice.write(ba);
        arq4.close();
      }

      arqIndice.close();
    } catch (Exception e) {
      String error = e.getMessage();
      System.out.println("Erro na finalização da OE: " + error);
    }

  }

  public void ordenacaoDistribuicao() {// Essa funcao está pegando 10 registros em 10 porem salvando no msm
    // arquivo que
    // é o arq 1, tem que intercalar pegou 10 arq 1 + 10 arq 2 + 10 arq2
    boolean eImpar = corrigirArquivoIndice();// essa funcao tem o objetivo de pegar o arquivo e ver se ele esta com a
    // quantidade de registros pares ou impares e corrigir o embaralhamento do
    // 0 caso seja impar para fazer a ordenacao
    arquivocrud arqcru = new arquivocrud();
    arqcru.deletaTudo(-1, 1, 1, -1, -1);

    try {

      RandomAccessFile arq1 = new RandomAccessFile("src/database/arq1.db", "rw");
      RandomAccessFile arq2 = new RandomAccessFile("src/database/arq2.db", "rw");
      RandomAccessFile arqI = new RandomAccessFile("src/database/aindices.db", "rw");

      long tamArquivoIndice = arqI.length();
      int inteirotamArquivoIndice = (int) tamArquivoIndice;// tamanho total do arquivo

      indice indiceArray[];
      indiceArray = new indice[10];// abri 10 casas de array do objeto indice

      int contadorParaSalvarNoArquivo1 = 0;
      int contadorArrayIndice = 0;
      int contadorPrincipal = 0;
      inteirotamArquivoIndice /= 13;// para pegar a qtd de elementos no arquivo (sem considerar a correcao do 0)
      int inteirotamArquivoIndice2 = inteirotamArquivoIndice;

      if (eImpar) {// caso a correcao indentifique que os ultimos registros não sao vazios ele
        // retira 1 valor do tamanho do arquivo, o valor adicionado pelo contador, caso
        // seja impar ele retira o valor referente ao contador + a correcao de 0
        inteirotamArquivoIndice2 -= 2;
      } else {
        inteirotamArquivoIndice2 -= 1;
      }

      if (inteirotamArquivoIndice != 0) {

        while (contadorPrincipal < inteirotamArquivoIndice) {// nao foi retirado o -1 da variavel
          // inteirotamArquivoIndice, pois ela faz o número correto
          // de loops em relacao ao tamanho do arquivo, caso seja
          // necessário um encerramento antes, ele entra no if logo
          // apos o qtdElementosPresentes

          Short idIndiceAD = arqI.readShort();
          indice ic = new indice();
          Long posiIndiceAD = arqI.readLong();
          String lapideAD = arqI.readUTF();
          ic.setIdIndice(idIndiceAD);
          ic.setPosiIndice(posiIndiceAD);
          ic.setLapide(lapideAD);
          indiceArray[contadorArrayIndice] = ic;

          if (contadorParaSalvarNoArquivo1 == 9 || contadorPrincipal == inteirotamArquivoIndice2
              || contadorParaSalvarNoArquivo1 == 19) {// aqui ele testa, se for 9 é que o array indice esta cheio, entao
            // ele ordena e salva, caso for 19 é que o 2 array de indice esta
            // cheio, e se contadorPrincipal = inteirotamArquivo, consiste que
            // chegou no final do arquivo

            int qtdElementosPresente = qtdElementoArrayIndice(indiceArray);
            if ((contadorPrincipal == inteirotamArquivoIndice2) && (contadorParaSalvarNoArquivo1 != 9)
                && (contadorParaSalvarNoArquivo1 != 19)) {// quando o contador for != 9 e != 19 mas igual ao tamanho do
              // arquivo ele ordena e encerra
              inteirotamArquivoIndice = inteirotamArquivoIndice2;
            } // precisa testar com o segundo caminho incompleto e com ele cheio.

            if (qtdElementosPresente != 1) {// ordenacao só quando tiver + de 1 elemento
              ic.quicksortIndice(indiceArray, 0, qtdElementosPresente - 1);
            }

            byte[] retornoByteArray;
            retornoByteArray = ic.toByteArray(indiceArray, qtdElementosPresente);
            indiceArray = new indice[10];
            contadorArrayIndice = -1;

            if (contadorParaSalvarNoArquivo1 >= 0 && contadorParaSalvarNoArquivo1 <= 9) {
              long ultimaPosidoArq1 = arq1.length();
              arq1.seek(ultimaPosidoArq1);
              arq1.write(retornoByteArray);
            }

            if (contadorParaSalvarNoArquivo1 > 9 && contadorParaSalvarNoArquivo1 < 20) {
              long ultimaPosidoArq2 = arq2.length();
              arq2.seek(ultimaPosidoArq2);
              arq2.write(retornoByteArray);
            }

            if (contadorParaSalvarNoArquivo1 == 19) {// quando chegar em 19 ele restaura o contador, pois esse contador
              // que sabe caso o array de indice fique cheio. Quando ele da 2
              // volta completas ele reseta para - 1 pois ja vai fazer um ++
              // antes de ler, então para comecar de 0
              contadorParaSalvarNoArquivo1 = -1;
            }

          }

          contadorParaSalvarNoArquivo1++;
          contadorArrayIndice++;
          contadorPrincipal++;

        }
      }

      ordenacaoExterna();

      arqI.close();
      arq1.close();
      arq2.close();

    } catch (Exception e) {
      String error = e.getMessage();
      System.out.println("Mensagem de Erro: " + error);
      return;
    }

  }

}
