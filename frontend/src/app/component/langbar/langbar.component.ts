import { Component, HostListener } from '@angular/core';
import { TranslationService } from 'src/app/service/translate/translation.service';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/service/authentication/authentication.service';
import { ToastrService } from 'ngx-toastr';
import { faBars } from '@fortawesome/free-solid-svg-icons';
import { faGlobe } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-langbar',
  templateUrl: './langbar.component.html',
  styleUrls: ['./langbar.component.css']
})
export class LangbarComponent {
  constructor(
    public authenticationService: AuthenticationService,
    private router: Router,
    private toast: ToastrService,
    private translationService: TranslationService
  ) { }

  faBars = faBars;
  faGlobe = faGlobe;

  showDropdown1 = false;
  showDropdown2 = false;

  toggleDropdown1() {
    this.showDropdown1 = !this.showDropdown1;
  }

  toggleDropdown2() {
    this.showDropdown2 = !this.showDropdown2;
  }

  isDropdown1Clicked(target: HTMLElement): boolean {
    const dropdownElement = document.querySelector('.dropdown');
    return dropdownElement?.contains(target);
  }

  isDropdown2Clicked(target: HTMLElement): boolean {
    const dropdownElement = document.querySelector('.dropdown2');
    return dropdownElement?.contains(target);
  }

  @HostListener('document:click', ['$event.target'])
  onDocumentClick(target: HTMLElement) {
    if (!this.isDropdown1Clicked(target)) {
      this.showDropdown1 = false;
    } 
    if (!this.isDropdown2Clicked(target)) {
      this.showDropdown2 = false;
    }
  }

  switchLanguage(lang: string) {
    this.translationService.switchLanguage(lang);
    this.showDropdown2 = false;
  }

  selectOption() {
    this.showDropdown1 = false;
  }

  getOwnProfileLink() {
    return `/users/${this.authenticationService.getAuthenticatedUserId()}`;
  }

  logout() {
    this.authenticationService.logout();
    this.router.navigate(['login']);
    this.toast.success('Logged out successfully');
  }
}
