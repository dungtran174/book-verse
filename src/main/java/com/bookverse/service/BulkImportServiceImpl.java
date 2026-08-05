package com.bookverse.service;

import com.bookverse.dto.BookRequestDto;
import com.bookverse.dto.BookResponseDto;
import com.bookverse.entity.Book;
import com.bookverse.exception.FileProcessingException;
import com.bookverse.mapper.BookMapper;
import com.bookverse.repository.BookRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Import sách hàng loạt từ file Excel (.xlsx) hoặc CSV (.csv).
 * Cột: title, author, isbn, year, category, rating, description
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BulkImportServiceImpl implements BulkImportService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CoverImageService coverImageService;

    @Override
    public List<BookResponseDto> importBooks(MultipartFile file, List<MultipartFile> images) {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new IllegalArgumentException("Tên file không hợp lệ");

        List<BookRequestDto> bookDtos;
        if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            bookDtos = parseExcel(file);
        } else if (filename.endsWith(".csv")) {
            bookDtos = parseCsv(file);
        } else {
            throw new IllegalArgumentException("Chỉ hỗ trợ file .xlsx hoặc .csv");
        }

        log.info("Import {} sách từ file: {}", bookDtos.size(), filename);

        List<BookResponseDto> results = new ArrayList<>();
        for (int i = 0; i < bookDtos.size(); i++) {
            BookRequestDto dto = bookDtos.get(i);
            Book book = bookMapper.toEntity(dto);
            Book saved = bookRepository.save(book);

            // Gắn ảnh nếu có (khớp theo thứ tự)
            if (images != null && i < images.size() && !images.get(i).isEmpty()) {
                try {
                    coverImageService.validateImageFile(images.get(i));
                    String coverPath = coverImageService.processAndSaveCover(saved.getId(), images.get(i));
                    saved.setCoverPath(coverPath);
                    saved = bookRepository.save(saved);
                } catch (Exception e) {
                    log.warn("Lỗi ảnh dòng {}: {}", i + 1, e.getMessage());
                }
            }

            BookResponseDto responseDto = bookMapper.toResponseDto(saved);
            results.add(responseDto);
        }

        log.info("Import thành công {} sách", results.size());
        return results;
    }

    /** Parse file Excel (.xlsx) */
    private List<BookRequestDto> parseExcel(MultipartFile file) {
        List<BookRequestDto> list = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            // Bỏ qua dòng header
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                BookRequestDto dto = BookRequestDto.builder()
                        .title(getCellString(row, 0))
                        .author(getCellString(row, 1))
                        .isbn(getCellString(row, 2))
                        .year(getCellInt(row, 3))
                        .category(getCellString(row, 4))
                        .rating(getCellDouble(row, 5))
                        .description(getCellString(row, 6))
                        .build();
                if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
                    list.add(dto);
                }
            }
        } catch (IOException e) {
            throw new FileProcessingException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
        return list;
    }

    /** Parse file CSV */
    private List<BookRequestDto> parseCsv(MultipartFile file) {
        List<BookRequestDto> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();
            // Bỏ qua header
            for (int i = 1; i < rows.size(); i++) {
                String[] cols = rows.get(i);
                BookRequestDto dto = BookRequestDto.builder()
                        .title(getCol(cols, 0))
                        .author(getCol(cols, 1))
                        .isbn(getCol(cols, 2))
                        .year(parseIntSafe(getCol(cols, 3)))
                        .category(getCol(cols, 4))
                        .rating(parseDoubleSafe(getCol(cols, 5)))
                        .description(getCol(cols, 6))
                        .build();
                if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
                    list.add(dto);
                }
            }
        } catch (IOException | CsvException e) {
            throw new FileProcessingException("Lỗi đọc file CSV: " + e.getMessage(), e);
        }
        return list;
    }

    // === Helpers ===
    private String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().isBlank() ? null : cell.getStringCellValue().trim();
    }

    private Integer getCellInt(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        try { return (int) cell.getNumericCellValue(); }
        catch (Exception e) { return parseIntSafe(cell.toString()); }
    }

    private Double getCellDouble(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        try { return cell.getNumericCellValue(); }
        catch (Exception e) { return parseDoubleSafe(cell.toString()); }
    }

    private String getCol(String[] cols, int i) {
        return (i < cols.length && !cols[i].isBlank()) ? cols[i].trim() : null;
    }

    private Integer parseIntSafe(String s) {
        if (s == null) return null;
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return null; }
    }

    private Double parseDoubleSafe(String s) {
        if (s == null) return null;
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return null; }
    }
}
