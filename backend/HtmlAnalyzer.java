package backend;

import java.net.URL;
import java.net.URI;
import java.util.*;
import java.util.regex.*;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;


public class HtmlAnalyzer {

    public static void main(String[] args) throws Exception {
        // 1. Inicia o banco de dados
        DatabaseManager.inicializarBanco();
        
        // 2. Inicia o Servidor Web em paralelo
        iniciarServidorWeb();

        // 3. Inicia o Menu do Terminal
        iniciarMenuTerminal();
    }

    private static void iniciarServidorWeb() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/analisar", (exchange) -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            
            String response;
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.contains("url=")) {
                        String urlParaAnalisar = query.split("url=")[1];
                        // Decodifica a URL caso venha com caracteres especiais
                        urlParaAnalisar = java.net.URLDecoder.decode(urlParaAnalisar, "UTF-8");
                        response = realizarAuditoria(urlParaAnalisar);
                    } else {
                        response = "Erro: Parâmetro URL ausente.";
                    }
                } catch (Exception e) {
                    response = "Erro no servidor: " + e.getMessage();
                }
            } else {
                response = "Método não permitido";
            }
            
            byte[] bytes = response.getBytes("UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        });

        server.setExecutor(null);
        server.start();
        System.out.println("[Web] Servidor rodando em http://localhost:8080");
    }

    private static void iniciarMenuTerminal() {
    // O recurso declarado aqui será fechado automaticamente ao fim do bloco
    try (Scanner userInput = new Scanner(System.in)) {
        boolean sistemaAtivo = true;

        while (sistemaAtivo) {
            System.out.println("\n=== Analisador de Qualidade HTML Moderno ===");
            System.out.println("1 - Analisar Site");
            System.out.println("2 - Sair");
            System.out.print("Escolha: ");
            
            // Verificação de segurança caso o fluxo de entrada seja interrompido
            if (!userInput.hasNextLine()) break;
            
            String op = userInput.nextLine().trim();

            if (op.equals("1")) {
                System.out.print("Digite a URL: ");
                if (userInput.hasNextLine()) {
                    String url = userInput.nextLine().trim();
                    System.out.println(realizarAuditoria(url));
                }
            } 
            else if (op.equals("2")) {
                System.out.print("Deseja reiniciar (R) ou fechar terminal (F)? ");
                if (userInput.hasNextLine()) {
                    String escolha = userInput.nextLine().trim();
                    if (escolha.equalsIgnoreCase("F")) {
                        sistemaAtivo = false;
                    }
                }
            } else {
                System.out.println("Opção inválida!");
            }
        }
    } // O Scanner fecha aqui automaticamente

    System.out.println("Encerrando...");
    System.exit(0); 
}


    // Retorna String para servir tanto ao Terminal e ao JS
    private static String realizarAuditoria(String urlStr) {
        StringBuilder relatorio = new StringBuilder();
        try {
            StringBuilder content = new StringBuilder();
            // Versão para conexão
            URL url = URI.create(urlStr).toURL();
            try (Scanner sc = new Scanner(url.openStream(), "UTF-8")) {
                while (sc.hasNextLine()) content.append(sc.nextLine()).append("\n");
            }

            String html = content.toString();
            Stack<TagInfo> stack = new Stack<>();
            Set<String> tiposTags = new TreeSet<>();
            List<String> erros = new ArrayList<>();
            int tagsCompletas = 0;
            int linksErro = 0;
            
            Pattern tagPattern = Pattern.compile("<(/?[a-zA-Z0-9]+)([^>]*)>");
            Matcher matcher = tagPattern.matcher(html);

            while (matcher.find()) {
                String tagName = matcher.group(1).toLowerCase();
                String attributes = matcher.group(2);
                int line = contarLinhas(html, matcher.start());

                if (tagName.startsWith("/")) {
                    String name = tagName.substring(1);
                    if (!stack.isEmpty() && stack.peek().name.equals(name)) {
                        stack.pop();
                        tagsCompletas++;
                    } else {
                        erros.add("Linha " + line + ": Tag de fechamento </" + name + "> sem abertura.");
                    }
                } else {
                    if (isSelfClosing(tagName) || attributes.endsWith("/")) {
                        tagsCompletas++;
                    } else {
                        stack.push(new TagInfo(tagName, line));
                    }
                }
                tiposTags.add(tagName.replace("/", ""));
                
                if (tagName.equals("a") && attributes.contains("href")) {
                    if (attributes.contains("href=\"\"") || attributes.contains("href=\"#\"")) {
                        linksErro++;
                    }
                }
            }

            while (!stack.isEmpty()) {
                TagInfo t = stack.pop();
                erros.add("Linha " + t.line + ": Tag <" + t.name + "> nunca foi fechada.");
            }

            // Montando o texto
            relatorio.append("\n--- RESUMO DA ANÁLISE ---\n");
            relatorio.append("Tags Completas: ").append(tagsCompletas).append("\n");
            relatorio.append("Tipos de Tags: ").append(tiposTags).append("\n");
            relatorio.append("Links Vazios/Quebrados: ").append(linksErro).append("\n");
            
            if (erros.isEmpty()) {
                relatorio.append("Resultado: O site está estruturado corretamente!");
            } else {
                relatorio.append("ERROS ENCONTRADOS:\n");
                for (String erro : erros) relatorio.append(erro).append("\n");
            }

        } catch (Exception e) {
            return "Erro ao conectar: " + e.getMessage();
        }
        return relatorio.toString();
    }

    private static boolean isSelfClosing(String tag) {
        return Arrays.asList("img", "br", "hr", "input", "meta", "link", "base").contains(tag);
    }

    private static int contarLinhas(String text, int index) {
        return text.substring(0, index).split("\n").length;
    }

    static class TagInfo {
        String name; int line;
        TagInfo(String n, int l) { this.name = n; this.line = l; }
    }
}
