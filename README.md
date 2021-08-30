# NeoCommandsLibrary
Desenvolvendo uma biblioteca de utilitários de linha de comando voltada para a criação de aplicativos android.

### Compatibilidade
A lib foi feita em `Java 1.7` para garantir que possa ser utilizada em qualquer projeto android.

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
Você pode tratar o `Process` através do método `StreamUtils.syncReadProcess(..)`, que ler o fluxo por meio de `StreamUtils.readInputStream(...)` e valida a resposta retornando uma implementação de `Result<E>`, o qual você pode verificar o resultado através dos métodos `isSuccess()`, `getResult()` e `getError()`.
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

#### Comandos para o dalvik
A classe `CommandUtils`, assim como outras da biblioteca abstrai alguns comandos para facilitar quem quer criar apps. Veja por exemplo o código a seguir que executa um código direto na maquina virtual dalvik.

```kotlin
CommandUtils.DALVIK.syncExec("-help").apply {
    if(isSuccess) {
        Log.d(commandDebug, result!!)
    } else {
        Log.e(commandDebug, error!!)
    }
}
```
equivale a
```kotlin
CommandUtils.syncExec("dalvikvm", "-help").apply {
    if(isSuccess) {
        Log.d(commandDebug, result!!)
    } else {
        Log.e(commandDebug, error!!)
    }
}
```
se seu aplicativo precisa executar alguma lib na maquina virtual android, você poderia utilizar o comando -cp como mostrado abaixo
```kotlin
CommandUtils.syncExec("dalvikvm", "-cp", ... ).apply {
    if(isSuccess) {
        Log.d(commandDebug, result!!)
    } else {
        Log.e(commandDebug, error!!)
    }
}
```
mas você pode simplificar utilizando o seguinte código

```kotlin
CommandUtils.DALVIK.CP.syncExec(...).apply {
    if(isSuccess) {
        Log.d(commandDebug, result!!)
    } else {
        Log.e(commandDebug, error!!)
    }
}
```
esses "macetes" estão presentes em várias classes da biblioteca, use ára facilitar a leitura.

#### AaptUtils
#### DxUtils
#### EcjUtils

