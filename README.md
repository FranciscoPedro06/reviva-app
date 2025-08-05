# Reviva‑App

Reviva é um aplicativo Android desenvolvido para armazenar memórias pessoais como imagens, vídeos, documentos e áudios, com um diferencial: o usuário define uma data mínima para visualizar cada memória. Até essa data, o conteúdo permanece bloqueado — criando uma experiência de lembrança no tempo certo.

## ✨ Funcionalidades

- Cadastro e login de usuários
- Login com Google (desativado atualmente)
- Armazenamento de memórias: imagem, vídeo, documento ou áudio
- Gravação de áudios direto pelo app
- Definição de data de desbloqueio para memórias
- Proteção de acesso às memórias antes da data definida

## 🔧 Tecnologias utilizadas

- **Java** (Android Studio)
- **Firebase Authentication** – Cadastro e login de usuários
- **Firebase Firestore** – Armazenamento de dados
- **Firebase Storage** – Armazenamento de mídias

## 📁 Estrutura do Projeto

app/
├── src/
│ └── main/
│ ├── java/com/reviva/app/
│ │ ├── activities/ # Telas e funcionalidades principais
│ │ ├── models/ # Modelos: Memory.java, User.java
│ │ └── utils/ # Configurações e integração com Firebase
│ └── res/ # Parte visual do app (XML)


## 🚀 Como usar

Atualmente, o aplicativo não está disponível na Play Store.

Para desenvolvedores interessados:
- Clone este repositório no Android Studio
- Configure seu projeto com as credenciais do Firebase
- Compile e execute em um dispositivo físico ou emulador Android

## 📜 Licença

Este projeto está licenciado sob a Licença MIT – consulte o arquivo [LICENSE](LICENSE) para detalhes.

---

Desenvolvido por Matheus Santana, Francisco Pedro.
