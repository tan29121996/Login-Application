import { Component, HostListener } from '@angular/core';
import { TranslationService } from 'src/app/service/translate/translation.service';

@Component({
  selector: 'app-langbar',
  templateUrl: './langbar.component.html',
  styleUrls: ['./langbar.component.css']
})
export class LangbarComponent {
  constructor(private translationService: TranslationService) {}

  showDropdown = false;

  toggleDropdown() {
    this.showDropdown = !this.showDropdown;
  }

  isDropdownClicked(target: HTMLElement): boolean {
    const dropdownElement = document.querySelector('.dropdown');
    return dropdownElement?.contains(target);
  }

  @HostListener('document:click', ['$event.target'])
  onDocumentClick(target: HTMLElement) {
    if (!this.isDropdownClicked(target)) {
      this.showDropdown = false;
    }
  }

  switchLanguage(lang: string) {
    this.translationService.switchLanguage(lang);
    this.showDropdown = false;
  }
}
