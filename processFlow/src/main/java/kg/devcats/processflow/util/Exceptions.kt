package kg.devcats.processflow.util

object NullProcessFlowRepositoryException : IllegalStateException("Didn't set processFlowRepositoryImpl to ProcessFlowConfigurator, call setProcessFlowRepositoryImpl before using library")