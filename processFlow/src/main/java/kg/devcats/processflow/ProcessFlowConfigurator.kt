package kg.devcats.processflow

object ProcessFlowConfigurator {

    var selfieInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/lkab/joy/light/selfi_full.png" }
    var simpleSelfieInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/lkab/joy/light/selfi_full.png" } //todo

    var recognizerTimeoutLimit = 20
    var recognizerTimeoutMills = 20000L
}