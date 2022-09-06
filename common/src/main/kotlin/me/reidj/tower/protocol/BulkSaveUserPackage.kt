package me.reidj.tower.protocol

import ru.cristalix.core.network.CorePackage

data class BulkSaveUserPackage(val packages: List<SaveUserPackage>): CorePackage()
