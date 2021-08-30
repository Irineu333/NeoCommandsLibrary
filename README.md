# NeoCommandsLibrary
Desenvolvendo uma biblioteca de utilitários de linha de comando voltada para a criação de aplicativos android.

### Compatibilidade
A lib foi feita em `Java 1.7` para garantir que possa ser utilizada em qualquer lugar.

### Algumas funções
#### Utilitários de linha de comando
Você pode executar facilmente comandos utilizando os utilitários de linha de comando, `CommandUtils`, do pacote _fundation_, que usam as apis de fluxo de dados e tratamento de erros e respostas também do pacote _fundation_.
```kotlin
CommandUtils.syncExec("echo Roi").apply {
    if (isSuccess) {
        Log.d(commandDebug, result!!)
    } else {
        Log.e(commandDebug, error!!)
    }
}
```

Você pode utilizar comandos assíncronos com ` CommandUtils.asyncExec(...)` que retorna uma instância de `Process`, que você pode tratar utilizando a api de de fluxo de dados `StreamUtils`.

#### Utilitários de fluxo de dados
Você pode tratar o `Process` através de base de `StreamUtils.syncReadProcess(..)`, que ler o fluxo por meio de `StreamUtils.readInputStream(...)` e valida a resposta retornando uma implementação de  `Result<E>`, o qual você pode verificar o resultado através dos métodos `isSuccess()`, `getResult()` e `getError()`.
```kotlin
val process: Process? = CommandUtils.asyncExec("echo Roi")

StreamUtils.syncReadProcess(process!!).apply {
    if (isSuccess) {
        Log.d(commandDebug, result!!)
    } else {
        Log.e(commandDebug, error!!)
    }
}
```

