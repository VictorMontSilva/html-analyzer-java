async function interagir() {
    const urlDigitada = document.getElementById("inputUrl").value;
    const painelResultado = document.getElementById("resultado");

    painelResultado.innerHTML = "Analisando...";

    try {
        // Faz a chamada para o Java
        const response = await fetch(`http://localhost:8080/analisar?url=${urlDigitada}`);
        const texto = await response.text();
        
        // Exibe o relatório no HTML
        painelResultado.innerText = texto;
    } catch (error) {
        painelResultado.innerText = "Erro ao conectar com o analisador Java.";
    }
}