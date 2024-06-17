package org.example;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        System.out.println("Bem-Vindo ao Conversor de Moedas! ");

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Digite um valor:");
            double valor = scanner.nextDouble();

            System.out.println("Digite uma opção: ");
            System.out.println("1) Dólar (USD) para Euro (EUR)");
            System.out.println("2) Euro (EUR) para Dólar (USD)");
            System.out.println("3) Real (BRL) para Dólar (USD)");
            System.out.println("4) Dólar (USD) para Real (BRL)");
            System.out.println("5) Euro (EUR) para Real (BRL)");
            System.out.println("6) Real (BRL) para Euro (EUR)");
            System.out.println("0. Sair");
            System.out.print("Opção: ");

            int opcao = scanner.nextInt();

            if (opcao != 0) {
                double taxaCambio = obterTaxaCambio(opcao);
                double valorConvertido = valor * taxaCambio;
                System.out.printf("Valor convertido: %.2f%n", valorConvertido);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao obter a taxa de câmbio.");
        } catch (JSONException e) {
            System.err.println("Erro ao analisar a resposta da API.");
        } catch (IllegalArgumentException e) {
            System.err.println("Opção inválida selecionada.");
        } finally {
            scanner.close(); // Fechar o scanner no bloco finally para garantir que ele seja fechado
        }
    }

    private static double obterTaxaCambio(int opcao) throws IOException, InterruptedException, JSONException {
        String base = "";
        String para = "";
        switch (opcao) {
            case 1:
                base = "USD";
                para = "EUR";
                break;
            case 2:
                base = "EUR";
                para = "USD";
                break;
            case 3:
                base = "BRL";
                para = "USD";
                break;
            case 4:
                base = "USD";
                para = "BRL";
                break;
            case 5:
                base = "EUR";
                para = "BRL";
                break;
            case 6:
                base = "BRL";
                para = "EUR";
                break;
            default:
                throw new IllegalArgumentException("Opção inválida.");
        }
        String chaveAPI = "4fcc6953391232a669226e49";
        String endereco = "https://v6.exchangerate-api.com/v6/" + chaveAPI + "/latest/" + base;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endereco))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        JSONObject json = new JSONObject(responseBody);
        if (!json.has("conversion_rates")) {
            throw new JSONException("A chave 'conversion_rates' não foi encontrada no JSON.");
        }
        if (!json.getJSONObject("conversion_rates").has(para)) {
            throw new JSONException("A taxa de câmbio para '" + para + "' não foi encontrada.");
        }
        return json.getJSONObject("conversion_rates").getDouble(para);
    }
}