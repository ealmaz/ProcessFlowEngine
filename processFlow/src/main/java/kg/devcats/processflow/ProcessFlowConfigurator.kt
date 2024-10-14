package kg.devcats.processflow

object ProcessFlowConfigurator {

    var selfieInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/lkab/joy/light/selfi_full.png" }
    var simpleSelfieInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/media-service/light/selfie.png" }
    var foreignPassportInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/media-service/dark/passport.png" }

    var recognizerTimeoutLimit = 20
    var recognizerTimeoutMills = 20000L
}