package com.reditus.novelcia.interfaces.species

import com.reditus.novelcia.domain.UpsertResult
import com.reditus.novelcia.domain.novel.application.SpeciesModel
import com.reditus.novelcia.domain.novel.application.SpeciesService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "Species", description = "소설 분류")
@RestController
class SpeciesController(
    private val speciesService: SpeciesService,
) {

    @Operation(summary = "소설 분류 목록 조회")
    @GetMapping("/api/species")
    fun getSpecies(): List<SpeciesModel> {
        return speciesService.getAllSpecies()
    }

    @Operation(summary = "소설 분류 업서트", description = "여러개 생성 혹은 수정 후, 결과 반환")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/admin/species")
    fun createSpecies(
        @RequestBody req: SpeciesReq.UpsertMany,
    ): UpsertResult<Long> {
        return speciesService.upsertSpecies(req.toCommands())
    }
}