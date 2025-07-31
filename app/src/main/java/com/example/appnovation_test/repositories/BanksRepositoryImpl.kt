package com.example.appnovation_test.repositories

import android.content.Context
import com.example.appnovation_test.model.Action
import com.example.appnovation_test.model.ActionDetail
import com.example.appnovation_test.model.BankInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject

class BanksRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BanksRepository {
    override suspend fun invoke(): Result<List<BankInfo>> {
        return try {
            // read json file from assets
            val inputStream = context.assets.open(BANK_JSON_FILE_NAME)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            // start parsing the JSON string
            val jsonObject = JSONObject(jsonString)
            val banksArray = jsonObject.getJSONArray("banks")

            val banksList = mutableListOf<BankInfo>()
            
            for (i in 0 until banksArray.length()) {
                val bankJson = banksArray.getJSONObject(i)
                
                // Extract bank data
                val nameEn = bankJson.getString("nameEn")
                val descEn = bankJson.optString("descEn", "") // Use optString in case descEn is missing
                val actionsJson = bankJson.getJSONObject("actions")

                // extract recommended action
                val recommendedArray = actionsJson.getJSONArray("recommended")
                val recommendedList = mutableListOf<ActionDetail>()
                for (j in 0 until recommendedArray.length()) {
                    val actionJson = recommendedArray.getJSONObject(j)
                    recommendedList.add(
                        ActionDetail(
                            titleEn = actionJson.getString("titleEn"),
                            type = ActionDetail.fromString(actionJson.getString("type")),
                            urlEn = actionJson.getString("urlEn")
                        )
                    )
                }

                // extract more action
                val moreArray = actionsJson.getJSONArray("more")
                val moreList = mutableListOf<ActionDetail>()
                for (j in 0 until moreArray.length()) {
                    val actionJson = moreArray.getJSONObject(j)
                    moreList.add(
                        ActionDetail(
                            titleEn = actionJson.getString("titleEn"),
                            type =  ActionDetail.fromString(actionJson.getString("type")),
                            urlEn = actionJson.getString("urlEn")
                        )
                    )
                }
                
                // Create BankInfo object
                val bankInfo = BankInfo(
                    nameEn = nameEn,
                    descEn = descEn,
                    actions = Action(
                        recommended = recommendedList,
                        more = moreList
                    )
                )
                
                banksList.add(bankInfo)
            }
            Result.success(banksList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val BANK_JSON_FILE_NAME = "banks.json"
    }
}