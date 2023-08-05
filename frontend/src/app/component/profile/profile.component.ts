import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Form, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { ActivatedRoute } from '@angular/router';
import { AuthenticationService } from 'src/app/service/authentication/authentication.service';
import { UserService } from 'src/app/service/user/user.service';
import { faSignOut } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  id: number;
  loading = false;
  createPostForm!: FormGroup;
  faSignOut = faSignOut;
  userId: number;
  items: any = [];
  item: any;
  user: any = {};
  userItem: any = {};

  openUser: boolean = false;

  defaultProfileImage =
    'https://w7.pngwing.com/pngs/754/2/png-transparent-samsung-galaxy-a8-a8-user-login-telephone-avatar-pawn-blue-angle-sphere-thumbnail.png';

  isModalOpen: boolean = false;
  clickedPost: any;

  @ViewChild('imageInput') imageInput: any;
  @ViewChild('videoInput') videoInput: any;

  imagePreviewUrl: any = '';
  videoPreviewUrl: any = '';

  constructor(
    private fb: FormBuilder,
    public authenticationService: AuthenticationService,
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private router: Router,
    private toast: ToastrService
  ) {}

  ngOnInit(): void {
    
    this.createPostForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(50)]],
      caption: ['', [Validators.required]],
      link: [''],
      file: [null],
    });

    this.activatedRoute.params.subscribe({
      next: (params) => {
        this.userId = +params['id'];
        this.getUserById(this.userId);
      },
    });

    this.id = this.authenticationService.getAuthenticatedUserId();
  }

  logout() {
    this.authenticationService.logout();
    this.router.navigate(['login']);
    this.toast.success('Logged out successfully');
  }

  private getUserById(id: number) {
    this.userService.getUserById(id).subscribe({
      next: (user) => {
        console.log(user);
        this.user = user;
      },
    });
  }
}
